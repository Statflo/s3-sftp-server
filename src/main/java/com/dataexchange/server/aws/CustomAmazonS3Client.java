package com.dataexchange.server.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.InputStream;

/**
 * http://docs.aws.amazon.com/AmazonS3/latest/dev/SSEUsingJavaSDK.html
 */
public class CustomAmazonS3Client extends AmazonS3Client {

    public CustomAmazonS3Client(AWSCredentialsProvider credentialsProvider, ClientConfiguration clientConfiguration,
                                RequestMetricCollector requestMetricsCollector) {
        super(credentialsProvider, clientConfiguration, requestMetricsCollector);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws SdkClientException {
        metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

        return putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws SdkClientException {
        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (newObjectMetadata == null) {
            newObjectMetadata = new ObjectMetadata();
            copyObjectRequest.setNewObjectMetadata(newObjectMetadata);
        }
        newObjectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

        return super.copyObject(copyObjectRequest);
    }
}
