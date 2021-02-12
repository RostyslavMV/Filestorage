package com.rmv.filestorage.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Document(indexName = "storage", indexStoreType = "file")
@Getter
@Setter
public class File {

    @Id
    private String ID;

    @NotBlank(message = "Name of file must not be blank")
    private String name;

    @NotNull(message = "There must be a size of a file")
    @Min(value = 0L,message = "Size of file must be positive")
    private long size;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }
}
