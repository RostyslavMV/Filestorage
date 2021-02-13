package com.rmv.filestorage.dto;

import com.rmv.filestorage.model.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FilesPage {
    private long total;
    private List<File> page;
}
