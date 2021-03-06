package com.rmv.filestorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileIdInfoDTO {
    @JsonProperty("ID")
    String ID;
}
