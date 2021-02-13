package com.rmv.filestorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorInfoDTO extends SuccessInfoDTO {
    private String error;

    public ErrorInfoDTO(String message) {
        this.success = false;
        this.error = message;
    }
}
