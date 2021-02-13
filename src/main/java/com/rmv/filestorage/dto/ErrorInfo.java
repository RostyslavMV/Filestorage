package com.rmv.filestorage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorInfo extends SuccessInfo {
    private String error;

    public ErrorInfo(String message) {
        this.success = false;
        this.error = message;
    }
}
