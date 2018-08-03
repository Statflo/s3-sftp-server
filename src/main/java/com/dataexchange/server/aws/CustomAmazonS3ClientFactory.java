package com.dataexchange.server.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import com.upplication.s3fs.AmazonS3ClientFactory;
import com.dataexchange.server.util.ApplicationContextProvider;
import com.dataexchange.server.util.LocalstackConfigurator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class CustomAmazonS3ClientFactory extends AmazonS3ClientFactory {

    private static AmazonS3 singletonClient = null;

    @Override
    protected AmazonS3 createAmazonS3(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration,
                                      RequestMetricCollector requestMetricsCollector) {

        if (singletonClient != null) {
            return singletonClient;
        }

        final CustomAmazonS3Client client = new CustomAmazonS3Client(credentialsProvider, clientConfiguration, requestMetricsCollector);
        final Environment environment = getApplicationContext().getEnvironment();

        // Configure localstack if enabled
        LocalstackConfigurator.configureIfEnabled(client, environment);

        singletonClient = client;

        return singletonClient;
    }

    @Override
    protected AWSCredentialsProvider getCredentialsProvider(Properties props) {
        try {
            return getApplicationContext().getBean(AWSCredentialsProvider.class);
        } catch (NoSuchBeanDefinitionException e) {
            return super.getCredentialsProvider(props);
        }
    }

    protected ApplicationContext getApplicationContext() {
        return ApplicationContextProvider.getApplicationContext();
    }
}
