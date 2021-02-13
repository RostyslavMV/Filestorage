package com.rmv.filestorage.controller;

import com.rmv.filestorage.dto.FileIdInfo;
import com.rmv.filestorage.dto.FilesPage;
import com.rmv.filestorage.dto.SuccessInfo;
import com.rmv.filestorage.exception.BadRequestException;
import com.rmv.filestorage.exception.FileNotFoundInRepositoryException;
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
import java.io.FileNotFoundException;
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
    @ResponseBody
    public FileIdInfo uploadFile(@RequestBody @Valid File file,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorText = bindingResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException(errorText);
        }

        File savedFile = fileRepository.save(file);
        return new FileIdInfo(savedFile.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public SuccessInfo deleteFile(@PathVariable String id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException("file not found");
        }
        fileRepository.delete(optionalFile.get());
        return new SuccessInfo(true);
    }

    @PostMapping("/{id}/tags")
    @ResponseBody
    public SuccessInfo assignTags(@PathVariable String id,
                                  @RequestBody Set<String> tags) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException("file not found");
        }
        File file = optionalFile.get();
        file.getTags().addAll(tags);
        fileRepository.save(file);
        return new SuccessInfo(true);
    }

    @DeleteMapping("/{id}/tags")
    @ResponseBody
    public SuccessInfo removeTags(@PathVariable String id,
                                  @RequestBody Set<String> tags) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException("file not found");
        }
        File file = optionalFile.get();
        Set<String> fileTags = file.getTags();

        for (String tag : tags) {
            if (!fileTags.contains(tag))
                throw new BadRequestException("tag not found on file");
        }

        for (String tag : tags) {
            fileTags.remove(tag);
        }
        fileRepository.save(file);
        return new SuccessInfo(true);
    }

    @GetMapping
    @ResponseBody
    public FilesPage listFilesWithPagination(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String... tags) {

        Page<File> filesPage;
        if (tags == null || tags.length == 0) {
            filesPage = fileRepository.findAll(PageRequest.of(page, size));
        } else {
            Set<String> tagsSet = new HashSet<>(Arrays.asList(tags));
            filesPage = fileRepository.findAllByTags(tagsSet, PageRequest.of(page, size));
        }

        return new FilesPage(filesPage.getTotalElements(), filesPage.getContent());
    }
}
