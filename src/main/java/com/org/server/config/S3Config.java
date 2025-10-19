// package com.org.server.config;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
// import software.amazon.awssdk.auth.credentials.AwsCredentials;
// import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
// import software.amazon.awssdk.regions.Region;
// import software.amazon.awssdk.services.s3.S3Client;
// import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//
// @Configuration
// public class S3Config {
//
//     @Value("${spring.cloud.aws.credential.access-key}")
//     private String accessKey;
//     @Value("${spring.cloud.aws.credential.secret-key}")
//     private String secretKey;
//     @Value("${spring.cloud.aws.region.static}")
//     private String region;
//
//     @Bean
//     public S3Client awsS3Client(){
//         AwsBasicCredentials awsBasicCredentials=AwsBasicCredentials.create(accessKey,secretKey);
//         return S3Client.builder()
//                 .region(Region.of(region))
//                 .credentialsProvider(new AwsCredentialsProvider() {
//                     @Override
//                     public AwsCredentials resolveCredentials() {
//                         return awsBasicCredentials;
//                     }
//                 })
//                 .build();
//     }
//
//     @Bean
//     public S3Presigner s3Presigner(){
//         AwsBasicCredentials awsBasicCredentials=AwsBasicCredentials.create(accessKey,secretKey);
//         return S3Presigner.builder()
//                 .region(Region.of(region))
//                 .credentialsProvider(new AwsCredentialsProvider() {
//                     @Override
//                     public AwsCredentials resolveCredentials() {
//                         return awsBasicCredentials;
//                     }
//                 })
//                 .build();
//     }
// }