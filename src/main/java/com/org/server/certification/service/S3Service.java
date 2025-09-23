package com.org.server.certification.service;



import com.org.server.exception.MoiraException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.util.UUID;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import com.org.server.util.MediaType;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Presigner presigner;
    private final S3Client s3Client;

    public String getPagePreSignUrl(String fileLocation){
        GetObjectRequest getObjectRequest=GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileLocation)
                .build();
        GetObjectPresignRequest getObjectPresignRequest=GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(5L))
                .build();
        return presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }

    public String updatePagePreSignUrl(String contentType,String fileLocation){
        PutObjectRequest putObjectRequest= PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileLocation)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest putObjectPresignRequest=PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(5L))
                .build();
        return presigner.presignPutObject(putObjectPresignRequest).url().toString();
    }

    public String savePagePreSignUrl(String contentType,String fileName){
        if(!verifyContentType(contentType)){
            throw new MoiraException("잘못된 파일입니다",HttpStatus.BAD_REQUEST);
        }
        String type=contentType.split("/")[0].equals("image") ? "image":"whiteboard";
        String fileLocation=type+"/"+UUID.randomUUID().toString()+"-"+fileName;
        PutObjectRequest putObjectRequest= PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileLocation)
                .contentType(contentType)
                .build();
        PutObjectPresignRequest putObjectPresignRequest=PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(5L))
                .build();
        return presigner.presignPutObject(putObjectPresignRequest).url().toString();
    }

    public void delPagePreSignUrl(String fileLocation){
        DeleteObjectRequest deleteObjectRequest=DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileLocation)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }


    private boolean verifyContentType(String contentType){
        for(MediaType mediaType:MediaType.values()){
            String type=mediaType.getValue();
            if(type.equals(contentType)){
                return true;
            }
        }
        return false;
    }
}
