package com.s3.validation.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSSNSConfig {

    @Autowired
    private AWSS3Config awss3Config;

    @Value("${region}")
    private String region;


    @Bean
    public AmazonSNS amazonSNS(){
        AmazonSNS sns = AmazonSNSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awss3Config.credentials()))
                .withRegion(region).build();
        return sns;

    }

}
