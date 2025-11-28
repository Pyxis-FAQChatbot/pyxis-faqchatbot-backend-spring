package com.pyxis.backend.common.s3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pyxis.backend.common.exception.CustomException;
import com.pyxis.backend.common.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final AmazonS3 ncpS3Client;

    @Value("${ncp.bucket}")
    private String bucket;

    @Value(("${ncp.endpoint}"))
    private String endpoint;

    public String upload(MultipartFile file) {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());


            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    fileName,
                    file.getInputStream(),
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            ncpS3Client.putObject(putObjectRequest);

        } catch (IOException e) {
            throw new CustomException(ErrorType.FILE_UPLOAD_FAILED);
        }

        return endpoint + "/" + bucket + "/" + fileName;
    }

    public void delete(String imageUrl) {
        try {
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            ncpS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new CustomException(ErrorType.FILE_DELETE_FAILED);
        }
    }
}
