package com.rmv.filestorage.service;

import com.rmv.filestorage.model.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileExtensionToTagServiceTest {

    @InjectMocks
    private FileExtensionToTagService fileExtensionToTagService;

    private final Map<String, String> testMap = new HashMap<>();

    @BeforeEach
    public void setUp() {
        testMap.put("mp3", "audio");
        testMap.put("txt", "document");
        ReflectionTestUtils.setField(fileExtensionToTagService, "extensionToTagMap", testMap);
    }

    @Test
    void getTagByExtension(){
        File file = new File("name.txt", 456465);
        fileExtensionToTagService.getTagByExtension(file);
        assertTrue(file.getTags().contains(testMap.get("txt")));

        file = new File("name.mp3", 456465);
        fileExtensionToTagService.getTagByExtension(file);
        assertTrue(file.getTags().contains(testMap.get("mp3")));

        file = new File("name.mp88", 456465);
        fileExtensionToTagService.getTagByExtension(file);
        assertTrue(file.getTags().isEmpty());
    }
}
