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
public class FileService {

    private static final String FILE_NOT_FOUND = "file not found";
    private static final String TAG_NOT_FOUND = "tag not found on file";

    private final FileRepository fileRepository;
    private final FileExtensionToTagService fileExtensionToTagService;

    public File save(File file) {
        fileExtensionToTagService.getTagByExtension(file);
        return fileRepository.save(file);
    }

    public void deleteById(String id) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException(FILE_NOT_FOUND);
        }
        fileRepository.delete(optionalFile.get());
    }

    public void assignTagsById(String id, Set<String> tags) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException(FILE_NOT_FOUND);
        }
        File file = optionalFile.get();
        file.getTags().addAll(tags);
        fileRepository.save(file);
    }

    public void removeTagsById(String id, Set<String> tags) {
        Optional<File> optionalFile = fileRepository.findById(id);
        if (optionalFile.isEmpty()) {
            throw new FileNotFoundInRepositoryException(FILE_NOT_FOUND);
        }
        File file = optionalFile.get();
        Set<String> fileTags = file.getTags();

        for (String tag : tags) {
            if (!fileTags.contains(tag))
                throw new BadRequestException(TAG_NOT_FOUND);
        }

        for (String tag : tags) {
            fileTags.remove(tag);
        }
        fileRepository.save(file);
    }

    public FilesPageDTO listFilesWithPagination(int page, int size, String namePart, String... tags) {
        Page<File> filesPage;
        namePart = namePart.replaceAll("(\\r\\n|\\n|\\r)", "");
        if (tags == null || tags.length == 0) {
            filesPage = fileRepository.findAllByNameContains(namePart, PageRequest.of(page, size));
        } else {
            Set<String> tagsSet = new HashSet<>(Arrays.asList(tags));
            filesPage = fileRepository.findAllByTagsAndNameContains(tagsSet, namePart, PageRequest.of(page, size));
        }
        return new FilesPageDTO(filesPage.getTotalElements(), filesPage.getContent());
    }
}
