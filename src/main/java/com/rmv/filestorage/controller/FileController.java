package com.rmv.filestorage.controller;

import com.rmv.filestorage.model.File;
import com.rmv.filestorage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileRepository fileRepository;

    @Autowired
    public FileController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping
    public ResponseEntity<HashMap<String, String>> uploadFile(@RequestBody @Valid File file,
                                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorText = bindingResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>(getErrorMap(errorText), HttpStatus.BAD_REQUEST);
        }

        File savedFile = fileRepository.save(file);
        HashMap<String, String> map = new HashMap<>();
        map.put("ID", savedFile.getId());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deleteFile(@PathVariable String id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return new ResponseEntity<>(getErrorMap("file not found"), HttpStatus.NOT_FOUND);
        }
        fileRepository.delete(optionalFile.get());
        return getSuccessResponseEntity();
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<HashMap<String, String>> assignTags(@PathVariable String id,
                                                              @RequestBody Set<String> tags) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return new ResponseEntity<>(getErrorMap("file not found"), HttpStatus.NOT_FOUND);
        }
        File file = optionalFile.get();
        file.getTags().addAll(tags);
        fileRepository.save(file);
        return getSuccessResponseEntity();
    }

    @DeleteMapping("/{id}/tags")
    public ResponseEntity<HashMap<String, String>> removeTags(@PathVariable String id,
                                                              @RequestBody Set<String> tags) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            return new ResponseEntity<>(getErrorMap("file not found"), HttpStatus.NOT_FOUND);
        }
        File file = optionalFile.get();
        Set<String> fileTags = file.getTags();

        for (String tag : tags) {
            if (!fileTags.contains(tag))
                return new ResponseEntity<>(getErrorMap("tag not found on file"),
                        HttpStatus.BAD_REQUEST);
        }

        for (String tag : tags) {
            fileTags.remove(tag);
        }
        fileRepository.save(file);
        return getSuccessResponseEntity();
    }

    @GetMapping
    public ResponseEntity<HashMap<String, Object>> listFilesWithPagination(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String... tags) {

        HashMap<String, Object> map = new HashMap<>();
        Page<File> filesPage;
        if (tags == null || tags.length == 0) {
            filesPage = fileRepository.findAll(PageRequest.of(page, size));
        } else {
            Set<String> tagsSet = new HashSet<>(Arrays.asList(tags));
            filesPage = fileRepository.findAllByTags(tagsSet, PageRequest.of(page, size));
        }
        map.put("total", Long.toString(filesPage.getTotalElements()));
        map.put("page", filesPage.getContent());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    private HashMap<String, String> getErrorMap(String errorText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("success", "false");
        map.put("error", errorText);
        return map;
    }

    private ResponseEntity<HashMap<String, String>> getSuccessResponseEntity() {
        HashMap<String, String> map = new HashMap<>();
        map.put("success", "true");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
