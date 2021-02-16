package com.rmv.filestorage.service;

import com.rmv.filestorage.dto.FilesPageDTO;
import com.rmv.filestorage.exception.BadRequestException;
import com.rmv.filestorage.model.File;
import com.rmv.filestorage.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockBean
    private FileRepository fileRepository;

    @Test
    void saveAddsTag() {
        File file = new File("name.mp3", 456465L);
        when(fileRepository.save(file)).thenReturn(file);
        File savedFile = fileService.save(file);
        assertTrue(savedFile.getTags().contains("audio"));
    }

    @Test
    void deleteById() {
        File file = new File("name.mp3", 456465L);
        when(fileRepository.findById(any())).thenReturn(Optional.of(file));
        assertDoesNotThrow(() -> fileService.deleteById(file.getId()));
    }

    @Test
    void assignTagsById() {
        File file = new File("name", 456465L);
        when(fileRepository.findById(any())).thenReturn(Optional.of(file));

        String tag1 = "tag1", tag2 = "tag2";
        fileService.assignTagsById(file.getId(), new HashSet<>(Arrays.asList("tag1", "tag2")));

        assertTrue(file.getTags().contains(tag1));
        assertTrue(file.getTags().contains(tag2));
        assertEquals(2, file.getTags().size());
    }

    @Test
    void removeTagsById() {
        File file = new File("name", 456465L);
        when(fileRepository.findById(any())).thenReturn(Optional.of(file));

        Set<String> tags = new HashSet<>(Arrays.asList("tag1", "tag2"));
        file.setTags(tags);
        fileService.removeTagsById(file.getId(), new HashSet<>(Arrays.asList("tag1", "tag2")));

        assertEquals(0, file.getTags().size());
    }

    @Test()
    void removeTagsByIdBadRequest() {
        File file = new File("name", 456465L);
        when(fileRepository.findById(any())).thenReturn(Optional.of(file));

        Set<String> tags = new HashSet<>(Arrays.asList("tag1", "tag2"));
        file.setTags(tags);

        assertThrows(BadRequestException.class,
                () -> fileService.removeTagsById(file.getId(),
                        new HashSet<>(Arrays.asList("tag2", "tag3"))));
        assertEquals(2, file.getTags().size());
    }

    @Test
    void listFilesWithPagination() {
        File file = new File("name", 456465L);
        file.getTags().add("tag1");

        PageImpl<File> page = new PageImpl<>(Collections.singletonList(file));
        when(fileRepository.findAllByNameContains(any(), any(PageRequest.class))).thenReturn(page);

        FilesPageDTO expectedPage = new FilesPageDTO(1, page.getContent());
        FilesPageDTO actualPage = fileService.listFilesWithPagination(0, 10, "");

        assertEquals(expectedPage.getTotal(), actualPage.getTotal());
        assertEquals(expectedPage.getPage(), actualPage.getPage());
    }
}
