package com.rmv.filestorage.service;

import com.rmv.filestorage.model.File;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileExtensionToTagService {

    private final Map<String, String> extensionToTagMap = new HashMap<>();

    {
        extensionToTagMap.put("mp3", "audio");
        extensionToTagMap.put("flac", "audio");
        extensionToTagMap.put("docx", "document");
        extensionToTagMap.put("doc", "document");
        extensionToTagMap.put("txt", "document");
        extensionToTagMap.put("mp4", "video");
        extensionToTagMap.put("avi", "video");
        extensionToTagMap.put("png", "image");
        extensionToTagMap.put("jpeg", "image");
        extensionToTagMap.put("jpg", "image");
    }

    public void getTagByExtension(File file) {
        String[] splittedFileName = file.getName().split("\\.");
        String extension = splittedFileName[splittedFileName.length - 1];
        if (extensionToTagMap.containsKey(extension)){
            file.getTags().add(extensionToTagMap.get(extension));
        }
    }
}
