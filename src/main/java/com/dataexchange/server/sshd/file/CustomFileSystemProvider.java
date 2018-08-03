package com.dataexchange.server.sshd.file;

import com.dataexchange.server.aws.CustomAmazonS3ClientFactory;
import com.google.common.collect.ImmutableMap;
import com.upplication.s3fs.S3FileSystemProvider;
import java.util.Map;

public class CustomFileSystemProvider extends S3FileSystemProvider {
    public static final Map<String, ?> ENV = ImmutableMap.<String, Object>builder()
        .put(S3FileSystemProvider.AMAZON_S3_FACTORY_CLASS, CustomAmazonS3ClientFactory.class.getName())
        .build();
}
