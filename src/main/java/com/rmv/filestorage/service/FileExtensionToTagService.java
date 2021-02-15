package com.rmv.filestorage.service;

import com.rmv.filestorage.model.File;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileExtensionToTagService {

    private static final String AUDIO = "audio";
    private static final String DOCUMENT = "document";
    private static final String VIDEO = "video";
    private static final String IMAGE = "image";

    private final Map<String, String> extensionToTagMap = new HashMap<>();

    {
        extensionToTagMap.put("mp3", AUDIO);
        extensionToTagMap.put("flac", AUDIO);
        extensionToTagMap.put("docx", DOCUMENT);
        extensionToTagMap.put("doc", DOCUMENT);
        extensionToTagMap.put("txt", DOCUMENT);
        extensionToTagMap.put("mp4", VIDEO);
        extensionToTagMap.put("avi", VIDEO);
        extensionToTagMap.put("png", IMAGE);
        extensionToTagMap.put("jpeg", IMAGE);
        extensionToTagMap.put("jpg", IMAGE);
    }

    public void getTagByExtension(File file) {
        String fileNameToLowerCase =  file.getName().toLowerCase();
        if (!fileNameToLowerCase.contains(".")){
            return;
        }
        String[] splittedFileName = file.getName().toLowerCase().split("\\.");
        String extension = splittedFileName[splittedFileName.length - 1];
        if (extensionToTagMap.containsKey(extension)){
            file.getTags().add(extensionToTagMap.get(extension));
        }
    }
}
