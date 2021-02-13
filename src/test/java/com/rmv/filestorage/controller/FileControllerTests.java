package com.rmv.filestorage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmv.filestorage.dto.ErrorInfoDTO;
import com.rmv.filestorage.dto.FilesPageDTO;
import com.rmv.filestorage.dto.SuccessInfoDTO;
import com.rmv.filestorage.model.File;
import com.rmv.filestorage.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private FileRepository fileRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void uploadFile() throws Exception {
        File file = new File("name", 456465);
        file.setId("id");
        String json = mapper.writeValueAsString(file);
        doReturn(file).when(fileRepository).save(any());

        MockHttpServletRequestBuilder msb = post("/file")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("ID", file.getId());
        String expectedJson = mapper.writeValueAsString(expectedResult);

        resultActions.andExpect(status().isOk()).andExpect(content().string(expectedJson));
    }

    @Test
    void deleteFileWhenFileExists() throws Exception {
        File file = new File("name", 456465);
        file.setId("id");

        when(fileRepository.findById(any())).thenReturn(Optional.of(file));
        doNothing().when(fileRepository).delete(any());

        MockHttpServletRequestBuilder msb = delete("/file/{ID}", "id");
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new SuccessInfoDTO(true));

        resultActions.andExpect(status().isOk()).andExpect(content().string(expectedJson));
    }

    @Test
    void deleteFileWhenFileNotExists() throws Exception {
        when(fileRepository.findById(any())).thenReturn(Optional.empty());
        doNothing().when(fileRepository).delete(any());

        MockHttpServletRequestBuilder msb = delete("/file/{ID}", "id");
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new ErrorInfoDTO("file not found"));

        resultActions.andExpect(status().isNotFound()).andExpect(content().string(expectedJson));
    }

    @Test
    void assignTagsWhenFileExists() throws Exception {
        File file = new File("name", 456465);
        file.setId("id");

        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");

        when(fileRepository.findById(any())).thenReturn(Optional.of(file));

        String json = mapper.writeValueAsString(tags);

        MockHttpServletRequestBuilder msb = post("/file/{ID}/tags", "id")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new SuccessInfoDTO(true));

        resultActions.andExpect(status().isOk()).andExpect(content().string(expectedJson));
    }

    @Test
    void assignTagsWhenFileNotExists() throws Exception {
        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");

        when(fileRepository.findById(any())).thenReturn(Optional.empty());

        String json = mapper.writeValueAsString(tags);

        MockHttpServletRequestBuilder msb = post("/file/{ID}/tags", "id")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new ErrorInfoDTO("file not found"));

        resultActions.andExpect(status().isNotFound()).andExpect(content().string(expectedJson));
    }

    @Test
    void removeTagsWhenFileExists() throws Exception {
        File file = new File("name", 456465);
        file.setId("id");

        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");
        file.setTags(tags);

        String json = mapper.writeValueAsString(tags);

        when(fileRepository.findById(any())).thenReturn(Optional.of(file));

        MockHttpServletRequestBuilder msb = delete("/file/{ID}/tags", "id")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new SuccessInfoDTO(true));

        resultActions.andExpect(status().isOk()).andExpect(content().string(expectedJson));
    }

    @Test
    void removeTagsWhenTagNotExists() throws Exception {
        File file = new File("name", 456465);
        file.setId("id");
        file.getTags().add("tag1");

        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");

        String json = mapper.writeValueAsString(tags);

        when(fileRepository.findById(any())).thenReturn(Optional.of(file));

        MockHttpServletRequestBuilder msb = delete("/file/{ID}/tags", "id")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new ErrorInfoDTO("tag not found on file"));

        resultActions.andExpect(status().isBadRequest()).andExpect(content().string(expectedJson));
    }

    @Test
    void removeTagsWhenFileNotExists() throws Exception {
        when(fileRepository.findById(any())).thenReturn(Optional.empty());

        Set<String> tags = new HashSet<>();
        tags.add("tag1");
        tags.add("tag2");

        String json = mapper.writeValueAsString(tags);

        MockHttpServletRequestBuilder msb = delete("/file/{ID}/tags", "id")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON);
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new ErrorInfoDTO("file not found"));

        resultActions.andExpect(status().isNotFound()).andExpect(content().string(expectedJson));
    }

    @Test
    void listFilesWithPagination() throws Exception {
        File file = new File("name", 456465);
        file.setId("id");
        file.getTags().add("tag1");

        PageImpl<File> page = new PageImpl<>(Collections.singletonList(file));

        when(fileRepository.findAll(any(PageRequest.class))).thenReturn(page);

        MockHttpServletRequestBuilder msb = get("/file");

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/file"))
                .build();

        ResultActions resultActions = mvc.perform(msb);

        String expectedJson = mapper.writeValueAsString(new FilesPageDTO(1, Collections.singletonList(file)));

        resultActions.andExpect(status().isOk()).andExpect(content().string(expectedJson));
    }

}
