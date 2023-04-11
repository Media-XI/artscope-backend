package com.example.codebase.util;

import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FileUtil {

    private static final Tika tika = new Tika();

    private static List<String> allowedImageTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/gif");
    private static List<String> allowedVideoTypes = List.of("video/mp4", "video/mov", "video/avi");
    private static List<String> allowedAudioTypes = List.of("audio/mp3", "audio/wav", "audio/ogg");

    public static boolean checkExtension(String extension) {
        return allowedImageTypes.contains("image/"+ extension) || allowedVideoTypes.contains("video/" + extension) || allowedAudioTypes.contains("audio/" + extension);
    }
    public static boolean validateFile(InputStream inputStream) throws IOException {
        String mimeType = tika.detect(inputStream);
        return allowedImageTypes.contains(mimeType) || allowedVideoTypes.contains(mimeType) || allowedAudioTypes.contains(mimeType);
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

}
