package com.rmv.filestorage.controller;

import com.rmv.filestorage.model.File;
import com.rmv.filestorage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
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
        HashMap<String, String> map = new HashMap<>();

        if (bindingResult.hasErrors()){
            map.put("success", "false");
            map.put("error", bindingResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        File savedFile = fileRepository.save(file);
        map.put("ID",savedFile.getID());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
