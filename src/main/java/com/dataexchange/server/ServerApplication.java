package com.dataexchange.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) throws IOException, URISyntaxException {
        SpringApplication.run(ServerApplication.class, args);
    }
}
