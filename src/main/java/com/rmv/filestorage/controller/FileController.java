package com.rmv.filestorage.controller;

import com.rmv.filestorage.dto.FileIdInfoDTO;
import com.rmv.filestorage.dto.FilesPageDTO;
import com.rmv.filestorage.dto.SuccessInfoDTO;
import com.rmv.filestorage.exception.BadRequestException;
import com.rmv.filestorage.model.File;
import com.rmv.filestorage.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FileController {

    private final FileService fileService;

    @PostMapping
    @ResponseBody
    public FileIdInfoDTO uploadFile(@RequestBody @Valid File file,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        }
        File savedFile = fileService.save(file);
        return new FileIdInfoDTO(savedFile.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public SuccessInfoDTO deleteFile(@PathVariable String id) {
        fileService.deleteById(id);
        return new SuccessInfoDTO(true);
    }

    @PostMapping("/{id}/tags")
    @ResponseBody
    public SuccessInfoDTO assignTags(@PathVariable String id,
                                     @RequestBody Set<String> tags) {
        fileService.assignTagsById(id, tags);
        return new SuccessInfoDTO(true);
    }

    @DeleteMapping("/{id}/tags")
    @ResponseBody
    public SuccessInfoDTO removeTags(@PathVariable String id,
                                     @RequestBody Set<String> tags) {
        fileService.removeTagsById(id, tags);
        return new SuccessInfoDTO(true);
    }

    @GetMapping
    @ResponseBody
    public FilesPageDTO listFilesWithPagination(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String... tags){

        return fileService.listFilesWithPagination(page,size,q,tags);
    }
}
