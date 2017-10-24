package com.dataexchange.server.aws;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.amazonaws.SDKGlobalConfiguration.*;

public class CustomAssumeRoleSessionCredentialsProvider implements AWSSessionCredentialsProvider {

    private static final int ROLE_SESSION_DURATION = 3600;
    private static final String roleSessionName = "sftp-server";

    private final String accessKey;
    private final String secretKey;
    private final String assumeRole;

    public CustomAssumeRoleSessionCredentialsProvider(String accessKey, String secretKey, String assumeRole) {
        Assert.hasText(accessKey, "Access Key must be provided");
        Assert.hasText(secretKey, "Secret Key must be provided");
        Assert.hasText(assumeRole, "Assume Role must be provided");
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.assumeRole = assumeRole;
        this.credentials = retrieveCredentials();
    }

    private LocalDateTime credentialsValid;
    private AWSSessionCredentials credentials;

    @Override
    public AWSSessionCredentials getCredentials() {
        if (ChronoUnit.SECONDS.between(credentialsValid, LocalDateTime.now()) >= (ROLE_SESSION_DURATION - 10)) {
            this.refresh();
        }
        return this.credentials;
    }

    @Override
    public void refresh() {
        this.credentials = retrieveCredentials();
    }

    private BasicSessionCredentials retrieveCredentials() {
        AWSSecurityTokenService securityTokenService = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(Regions.EU_WEST_1).build();

        AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
                .withRoleArn(assumeRole)
                .withDurationSeconds(ROLE_SESSION_DURATION) // Min 900 (15min) max 3600 (1h)
                .withRoleSessionName(roleSessionName);

        AssumeRoleResult assumeResult = securityTokenService.assumeRole(assumeRequest);

        setCredentialsValidSinceNow();
        System.setProperty(ACCESS_KEY_ENV_VAR, assumeResult.getCredentials().getAccessKeyId());
        System.setProperty(SECRET_KEY_ENV_VAR, assumeResult.getCredentials().getSecretAccessKey());
        System.setProperty(AWS_SESSION_TOKEN_ENV_VAR, assumeResult.getCredentials().getSessionToken());

        return new BasicSessionCredentials(assumeResult.getCredentials().getAccessKeyId(),
                assumeResult.getCredentials().getSecretAccessKey(),
                assumeResult.getCredentials().getSessionToken());
    }

    private void setCredentialsValidSinceNow() {
        this.credentialsValid = LocalDateTime.now();
    }

}
