package com.dataexchange.server.util;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class LocalstackConfigurator {

    public static void configureIfEnabled(final AmazonS3Client client, final Environment environment) {
        final Optional<String> optional = getEndpoint(environment, "s3");
        final Region region = Region.getRegion(Regions.DEFAULT_REGION);

        optional.ifPresent((endpoint) -> {
            setup(client, endpoint, region);

            // Use path style access to make it compatible with localstack
            // https://github.com/localstack/localstack/issues/43
            client.setS3ClientOptions(
                S3ClientOptions.builder()
                    .setPathStyleAccess(true)
                    .build()
            );
        });
    }

    public static void configureIfEnabled(final AWSSimpleSystemsManagement client, final Environment environment) {
        final Optional<String> optional = getEndpoint(environment, "ssm");
        final Region region = Region.getRegion(Regions.DEFAULT_REGION);

        optional.ifPresent((endpoint) -> {
            setup(client, endpoint, region);
        });
    }

    private static void setup(final Object client, final String endpoint, final Region region) {
        final Method setRegion = ReflectionUtils.findMethod(client.getClass(), "setRegion", Region.class);
        final Method setEndpoint = ReflectionUtils.findMethod(client.getClass(), "setEndpoint", String.class);

        if (setRegion == null || setEndpoint == null) {
            return;
        }

        invoke(client, () -> {
            ReflectionUtils.invokeMethod(setRegion, client, region);
            ReflectionUtils.invokeMethod(setEndpoint, client, endpoint);
        });
    }

    private static void invoke(final Object client, final Runnable runnable) {
        final Field immutableField = ReflectionUtils.findField(client.getClass(), "isImmutable");

        if (immutableField == null) {
            return;
        }

        immutableField.setAccessible(true);

        final Boolean immutableValue = (Boolean) ReflectionUtils.getField(immutableField, client);

        ReflectionUtils.setField(immutableField, client, false);

        runnable.run();

        ReflectionUtils.setField(immutableField, client, immutableValue);
        immutableField.setAccessible(false);
    }

    private static Optional<String> getEndpoint(final Environment environment, final String type) {
        final String enabled = environment.getProperty("localstack.enabled");
        final String typeEndpount = environment.getProperty("localstack." + type + ".url");
        final String typeEnabled = environment.getProperty("localstack." + type + ".enabled");

        if ( ! "true".equalsIgnoreCase(enabled) || ! "true".equalsIgnoreCase(typeEnabled)) {
            return Optional.empty();
        }

        return Optional.ofNullable(typeEndpount)
            .filter( url -> ! StringUtils.isEmpty(url));
    }
}