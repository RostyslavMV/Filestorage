package com.rmv.filestorage.service;

import com.rmv.filestorage.dto.FilesPageDTO;
import com.rmv.filestorage.exception.BadRequestException;
import com.rmv.filestorage.exception.FileNotFoundInRepositoryException;
import com.rmv.filestorage.model.File;
import com.rmv.filestorage.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FileService{

    private final FileRepository fileRepository;
    private final FileExtensionToTagService fIleExtensionToTagService;

    public File save(File file){
        fIleExtensionToTagService.getTagByExtension(file);
        return fileRepository.save(file);
    }

    public void deleteById(String id){
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException("file not found");
        }
        fileRepository.delete(optionalFile.get());
    }

    public void assignTagsById(String id, Set<String> tags){
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException("file not found");
        }
        File file = optionalFile.get();
        file.getTags().addAll(tags);
        fileRepository.save(file);
    }

    public void removeTagsById(String id, Set<String> tags){
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
    }

    public FilesPageDTO listFilesWithPagination(int page, int size, String...tags){
        Page<File> filesPage;
        if (tags == null || tags.length == 0) {
            filesPage = fileRepository.findAll(PageRequest.of(page, size));
        } else {
            Set<String> tagsSet = new HashSet<>(Arrays.asList(tags));
            filesPage = fileRepository.findAllByTags(tagsSet, PageRequest.of(page, size));
        }
        return new FilesPageDTO(filesPage.getTotalElements(), filesPage.getContent());
    }
}
