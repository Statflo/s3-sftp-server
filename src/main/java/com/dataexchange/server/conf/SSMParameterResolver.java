package com.dataexchange.server.conf;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.dataexchange.server.util.LocalstackConfigurator;
import java.lang.reflect.Field;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

@Component
public class SSMParameterResolver implements EnvironmentPostProcessor {

    private static final String PREFIX = "{ssmParameter}";

    private static final int PREFIX_LENGTH = PREFIX.length();

    private AWSSimpleSystemsManagement awsSsm = null;

    @Override
    public void postProcessEnvironment(
        final ConfigurableEnvironment environment,
        final SpringApplication springApplication) {

        StreamSupport.stream(environment.getPropertySources().spliterator(), false)
            .filter(propertySource -> propertySource instanceof EnumerablePropertySource)
            .forEach(propertySource -> {
                final EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource;
                final Map<String, Object> propertyOverrides = resolveSsmParameters(environment, enumerablePropertySource);

                if (!propertyOverrides.isEmpty()) {
                    PropertySource<?> processedProperties = new MapPropertySource("override-"+ propertySource.getName(), propertyOverrides);
                    environment.getPropertySources().addBefore(propertySource.getName(), processedProperties);
                }
            });
    }

    private Map<String, Object> resolveSsmParameters(
        final ConfigurableEnvironment environment,
        final EnumerablePropertySource propertySource) {
        return getPropertyNames(propertySource)
            .collect(Collectors.toMap(
                Function.identity(),
                name -> resolveSsmParameter(
                    environment,
                    propertySource.getProperty(name).toString()
                )
            ));
    }

    private Stream<String> getPropertyNames(final EnumerablePropertySource propertySource) {
        return Arrays.stream(propertySource.getPropertyNames())
            .filter(p -> (propertySource.getProperty(p) instanceof String))
            .filter(p -> ((String) propertySource.getProperty(p)).startsWith(PREFIX));
    }

    private String resolveSsmParameter(final ConfigurableEnvironment environment, final String name) {
        final String ssmParameterName = name.substring(PREFIX_LENGTH);
        final GetParametersRequest request = new GetParametersRequest();

        request.withNames(ssmParameterName);
        request.withWithDecryption(Boolean.TRUE);

        final AWSSimpleSystemsManagement client = getAWSClient(environment);
        final GetParametersResult result = client.getParameters(request);
        final String value = result.getParameters()
            .stream()
            .findFirst()
            .map(Parameter::getValue)
            .orElseThrow(() -> new RuntimeException("Unable to revolve : " + name));

        return value;
    }

    private AWSSimpleSystemsManagement getAWSClient(final ConfigurableEnvironment environment) {
        if (awsSsm != null) {
            return awsSsm;
        }

        final AWSSimpleSystemsManagement client = AWSSimpleSystemsManagementClientBuilder.defaultClient();

        LocalstackConfigurator.configureIfEnabled(client, environment);

        awsSsm = client;

        return awsSsm;
    }

    private void configureLocalStack(AWSSimpleSystemsManagement client, final String localStackUrl) {
        final Field isImmutableField = ReflectionUtils.findField(client.getClass(), "isImmutable");

        isImmutableField.setAccessible(true);
        ReflectionUtils.setField(isImmutableField, client, false);

        client.setEndpoint(localStackUrl);
        isImmutableField.setAccessible(false);
    }
}

