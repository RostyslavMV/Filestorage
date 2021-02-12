package com.rmv.filestorage.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(indexName = "storage", indexStoreType = "file")
@Getter
@Setter
public class File {

    @Id
    private String id;

    @NotBlank(message = "Name of file must not be blank")
    private String name;

    @NotNull(message = "There must be a size of a file")
    @Min(value = 0L,message = "Size of file must be positive")
    private long size;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<String> tags;

    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }
}
