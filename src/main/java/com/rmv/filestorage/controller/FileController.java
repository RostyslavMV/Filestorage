package com.rmv.filestorage.controller;

import com.rmv.filestorage.model.File;
import com.rmv.filestorage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private final FileRepository fileRepository;

    @Autowired
    public FileController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping("/file")
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

    @DeleteMapping("/file/{id}")
    public ResponseEntity<HashMap<String, String>> deleteFile(@PathVariable String id) {
        Optional<File> file = fileRepository.findById(id);
        if (file.isEmpty()) {
            return new ResponseEntity<>(getErrorMap("file not found"), HttpStatus.NOT_FOUND);
        }
        fileRepository.delete(file.get());
        return new ResponseEntity<>(getSucessMap(), HttpStatus.OK);
    }



    private HashMap<String, String> getErrorMap(String errorText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("success", "false");
        map.put("error", errorText);
        return map;
    }

    private HashMap<String, String> getSucessMap(){
        HashMap<String, String> map = new HashMap<>();
        map.put("success", "true");
        return map;
    }
}
