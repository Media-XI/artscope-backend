package com.example.codebase.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.codebase.domain.artwork.entity.ArtworkMedia;
import com.example.codebase.domain.post.entity.PostMedia;
import com.example.codebase.util.FileUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final AmazonS3 amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Getter
    @Value("${cloud.aws.s3.root-dir}")
    private String dir;

    public S3Service(AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public ResponseEntity<byte[]> getObject(String storedFileName) throws IOException {
        S3Object object = amazonS3Client.getObject(new GetObjectRequest(bucket, storedFileName));
        S3ObjectInputStream inputStream = object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(inputStream);

        String fileName = URLEncoder.encode(storedFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM));
        httpHeaders.setContentLength(bytes.length);
        // httpHeaders.setContentDispositionFormData("attachment", fileName); // 다운받을것이 아님

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    public String saveUploadFile(MultipartFile multipartFile) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        String originalFilename = multipartFile.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(index + 1).toLowerCase();

        if (!FileUtil.checkExtension(ext)) {
            throw new IOException("지원하지 않는 파일 확장자 입니다.");
        }

        String storeFileName = UUID.randomUUID() + "." + ext;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String key = dir + now + "/" + storeFileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            if (!FileUtil.validateFile(inputStream)) {
                throw new IOException("파일이 손상되었거나 지원하지 않는 확장자 입니다.");
            }
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, multipartFile.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        }
        return key;
    }

    public String saveUploadFile(String key, byte[] file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/xml");
        objectMetadata.setContentLength(file.length);

        InputStream inputStream = new ByteArrayInputStream(file);

        PutObjectResult putObjectResult = amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return key;
    }


    public void deleteObject(String url) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, url);
        amazonS3Client.deleteObject(deleteObjectRequest);
    }

    public void deleteObjects(List<String> urls) {
        List<DeleteObjectsRequest.KeyVersion> keyVersions = urls.stream()
            .map(DeleteObjectsRequest.KeyVersion::new)
            .collect(Collectors.toList());

        DeleteObjectsRequest deleteObjectRequest = new DeleteObjectsRequest(bucket)
            .withKeys(keyVersions);
        amazonS3Client.deleteObjects(deleteObjectRequest);
    }

    public void deleteS3Object(List<ArtworkMedia> artworkMedias) {
        for (ArtworkMedia artworkMedia : artworkMedias) {
            deleteObject(artworkMedia.getMediaUrl());
        }
    }

    public void deleteArtworkMediaS3Objects(List<ArtworkMedia> artworkMedias) {
        List<String> urls = artworkMedias.stream()
            .map(ArtworkMedia::getMediaUrl)
            .collect(Collectors.toList());
        deleteObjects(urls);
    }

    public void deletePostMediaS3Objects(List<PostMedia> medias) {
        List<String> urls = medias.stream()
            .map(PostMedia::getMediaUrl)
            .collect(Collectors.toList());
        if (urls.size() > 0) {
            deleteObjects(urls);
        }

        // TODO : 연관관계 삭제를 할지 고민
    }
}