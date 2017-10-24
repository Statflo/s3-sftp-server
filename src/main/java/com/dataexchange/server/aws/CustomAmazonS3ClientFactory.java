package com.dataexchange.server.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.services.s3.AmazonS3;
import com.upplication.s3fs.AmazonS3ClientFactory;
import com.dataexchange.server.util.ApplicationContextProvider;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Properties;

public class CustomAmazonS3ClientFactory extends AmazonS3ClientFactory {

    private static AmazonS3 singletonClient = null;

    @Override
    protected AmazonS3 createAmazonS3(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration,
                                      RequestMetricCollector requestMetricsCollector) {

        if (singletonClient == null) {
            singletonClient = new CustomAmazonS3Client(credentialsProvider, clientConfiguration, requestMetricsCollector);
        }

        return singletonClient;
    }

    @Override
    protected AWSCredentialsProvider getCredentialsProvider(Properties props) {
        try {
            return ApplicationContextProvider.getApplicationContext().getBean(AWSCredentialsProvider.class);
        } catch (NoSuchBeanDefinitionException e) {
            return super.getCredentialsProvider(props);
        }
    }

}
