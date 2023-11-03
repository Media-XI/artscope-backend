package com.example.codebase.domain.image.service;

import com.example.codebase.domain.artwork.dto.ArtworkCreateDTO;
import com.example.codebase.domain.artwork.dto.ArtworkMediaCreateDTO;
import com.example.codebase.domain.exhibition.dto.ExhbitionCreateDTO;
import com.example.codebase.domain.exhibition.dto.ExhibitionMediaCreateDTO;
import com.example.codebase.domain.post.dto.PostCreateDTO;
import com.example.codebase.domain.post.dto.PostMediaCreateDTO;
import com.example.codebase.s3.S3Service;
import com.example.codebase.util.FileUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

  private final S3Service s3Service;

  @Value("${app.file-count}")
  private String fileCount;

  public ImageService(S3Service s3Service) {
    this.s3Service = s3Service;
  }

  public void mediasUpload(ArtworkCreateDTO dto, List<MultipartFile> mediaFiles)
      throws IOException {
    if (mediaFiles.size() > Integer.valueOf(fileCount)) {
      throw new RuntimeException("파일은 최대 " + fileCount + "개까지 업로드 가능합니다.");
    }

    if (mediaFiles.size() == 0) {
      throw new RuntimeException("파일을 업로드 해주세요.");
    }

    for (int i = 0; i < dto.getMedias().size(); i++) {
      ArtworkMediaCreateDTO mediaDto = dto.getMedias().get(i);

      if (mediaDto.getMediaType().equals("url")) {
        String youtubeUrl = new String(mediaFiles.get(i).getBytes(), "UTF-8");

        if (!youtubeUrl.matches("^(https?\\:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.?be)\\/.+$")) {
          throw new RuntimeException(
              "유튜브 링크 형식이 올바르지 않습니다. ex) https://www.youtube.com/watch?v=XXXXXXXXXXX 또는 https://youtu.be/XXXXXXXXXXX");
        }

        mediaDto.setMediaUrl(youtubeUrl);
      } else {
        // 이미지 파일이면 원본 이미지의 사이즈를 구합니다.
        if (mediaDto.getMediaType().equals("image")) {
          BufferedImage bufferedImage =
              FileUtil.getBufferedImage(mediaFiles.get(i).getInputStream());
          mediaDto.setImageSize(bufferedImage);
        }
        // 파일 업로드
        String savedUrl = s3Service.saveUploadFile(mediaFiles.get(i));
        mediaDto.setMediaUrl(savedUrl);
      }
    }
  }

  // TODO : 중복코드 리팩터링
  public void mediasUpload(PostCreateDTO dto, List<MultipartFile> mediaFiles) throws IOException {
    if (mediaFiles.size() > Integer.valueOf(fileCount)) {
      throw new RuntimeException("파일은 최대 " + fileCount + "개까지 업로드 가능합니다.");
    }

    if (mediaFiles.size() == 0) {
      throw new RuntimeException("파일을 업로드 해주세요.");
    }

    for (int i = 0; i < dto.getMedias().size(); i++) {
      PostMediaCreateDTO mediaDto = dto.getMedias().get(i);

      if (mediaDto.getMediaType().equals("url")) {
        String youtubeUrl = new String(mediaFiles.get(i).getBytes(), "UTF-8");

        if (!youtubeUrl.matches("^(https?\\:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.?be)\\/.+$")) {
          throw new RuntimeException(
              "유튜브 링크 형식이 올바르지 않습니다. ex) https://www.youtube.com/watch?v=XXXXXXXXXXX 또는 https://youtu.be/XXXXXXXXXXX");
        }

        mediaDto.setMediaUrl(youtubeUrl);
      } else {
        // 이미지 파일이면 원본 이미지의 사이즈를 구합니다.
        if (mediaDto.getMediaType().equals("image")) {
          BufferedImage bufferedImage =
              FileUtil.getBufferedImage(mediaFiles.get(i).getInputStream());
          mediaDto.setImageSize(bufferedImage);
        }
        // 파일 업로드
        String savedUrl = s3Service.saveUploadFile(mediaFiles.get(i));
        mediaDto.setMediaUrl(savedUrl);
      }
    }
  }

  public void thumbnailUpload(ArtworkMediaCreateDTO thumbnailDto, MultipartFile thumbnailFile)
      throws IOException {
    String mediaType = thumbnailDto.getMediaType();

    if (!mediaType.equals("image") && FileUtil.validateImageFile(thumbnailFile.getInputStream())) {
      throw new RuntimeException("썸네일은 이미지 파일만 업로드 가능합니다.");
    }
    // 썸네일 파일 이미지 사이즈 구하기
    BufferedImage bufferedImage = FileUtil.getBufferedImage(thumbnailFile.getInputStream());
    thumbnailDto.setImageSize(bufferedImage);
    // 썸네일 업로드
    String savedUrl = s3Service.saveUploadFile(thumbnailFile);
    thumbnailDto.setMediaUrl(savedUrl);
  }

  public void thumbnailUpload(PostMediaCreateDTO thumbnailDto, MultipartFile thumbnailFile)
      throws IOException {
    String mediaType = thumbnailDto.getMediaType();

    if (!mediaType.equals("image") && FileUtil.validateImageFile(thumbnailFile.getInputStream())) {
      throw new RuntimeException("썸네일은 이미지 파일만 업로드 가능합니다.");
    }
    // 썸네일 파일 이미지 사이즈 구하기
    BufferedImage bufferedImage = FileUtil.getBufferedImage(thumbnailFile.getInputStream());
    thumbnailDto.setImageSize(bufferedImage);
    // 썸네일 업로드
    String savedUrl = s3Service.saveUploadFile(thumbnailFile);
    thumbnailDto.setMediaUrl(savedUrl);
  }

  public void mediasUpload(ExhbitionCreateDTO dto, List<MultipartFile> mediaFiles)
      throws IOException {
    if (mediaFiles.size() > Integer.valueOf(fileCount)) {
      throw new RuntimeException("파일은 최대 " + fileCount + "개까지 업로드 가능합니다.");
    }

    if (mediaFiles.size() == 0) {
      throw new RuntimeException("파일을 업로드 해주세요.");
    }

    for (int i = 0; i < dto.getMedias().size(); i++) {
      ExhibitionMediaCreateDTO mediaDto = dto.getMedias().get(i);

      if (mediaDto.getMediaType().equals("url")) {
        String youtubeUrl = new String(mediaFiles.get(i).getBytes(), "UTF-8");

        if (!youtubeUrl.matches("^(https?\\:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.?be)\\/.+$")) {
          throw new RuntimeException(
              "유튜브 링크 형식이 올바르지 않습니다. ex) https://www.youtube.com/watch?v=XXXXXXXXXXX 또는 https://youtu.be/XXXXXXXXXXX");
        }

        mediaDto.setMediaUrl(youtubeUrl);
        String savedUrl = s3Service.saveUploadFile(mediaFiles.get(i));
        mediaDto.setMediaUrl(savedUrl);
      } else {
        String savedUrl = s3Service.saveUploadFile(mediaFiles.get(i));
        mediaDto.setMediaUrl(savedUrl);
      }
    }
  }

  public void thumbnailUpload(ExhibitionMediaCreateDTO thumbnailDto, MultipartFile thumbnailFile)
      throws IOException {
    String mediaType = thumbnailDto.getMediaType();

    if (!mediaType.equals("image") && FileUtil.validateImageFile(thumbnailFile.getInputStream())) {
      throw new RuntimeException("썸네일은 이미지 파일만 업로드 가능합니다.");
    }
    String savedUrl = s3Service.saveUploadFile(thumbnailFile);
    thumbnailDto.setMediaUrl(savedUrl);
  }
}
