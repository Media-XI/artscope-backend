package com.example.codebase.util;

import org.apache.tika.Tika;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FileUtil {

    private static final Tika tika = new Tika();

    private static final List<String> allowedImageTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/gif",
        "image/bmp", "image/webp");
    private static final List<String> allowedVideoTypes = List.of("video/mp4", "video/mov", "video/avi", "video/quicktime",
        "video/webm", "video/x-msvideo", "video/x-ms-wmv");
    private static final List<String> allowedAudioTypes = List.of("audio/mp3", "audio/wav", "audio/ogg", "audio/mpeg",
        "audio/webm", "audio/x-m4a", "audio/x-ms-wma", "audio/x-ms-wax", "audio/x-ms-wmv");

    public static boolean checkExtension(String extension) {
        return allowedImageTypes.contains("image/" + extension) || allowedVideoTypes.contains("video/" + extension)
            || allowedAudioTypes.contains("audio/" + extension);
    }

    public static boolean checkImageExtension(String extension) {
        return allowedImageTypes.contains("image/" + extension);
    }

    public static boolean validateFile(InputStream inputStream) throws IOException {
        String mimeType = tika.detect(inputStream);
        return allowedImageTypes.contains(mimeType) || allowedVideoTypes.contains(mimeType)
            || allowedAudioTypes.contains(mimeType);
    }

    public static boolean validateImageFile(InputStream inputStream) throws IOException {
        String mimeType = tika.detect(inputStream);
        return allowedImageTypes.contains(mimeType);
    }

    public static boolean validateVideoFile(InputStream inputStream) throws IOException {
        String mimeType = tika.detect(inputStream);
        return allowedVideoTypes.contains(mimeType);
    }

    public static boolean validateAudioFile(InputStream inputStream) throws IOException {
        String mimeType = tika.detect(inputStream);
        return allowedAudioTypes.contains(mimeType);
    }

    public static BufferedImage getBufferedImage(InputStream inputStream) throws IOException {
        return Optional.ofNullable(ImageIO.read(inputStream))
            .orElseThrow(() -> new NoSuchElementException("이미지 파일이 아닙니다."));
    }
}
