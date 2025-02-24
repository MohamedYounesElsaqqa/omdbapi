package com.omdb.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ErrorResponse {
    private String massage;
    private Boolean success;
    private LocalDateTime dateTime;

    public ErrorResponse() {
    }

    public ErrorResponse(String massage) {
        this.massage = massage;
        this.dateTime = LocalDateTime.now();
        this.success = Boolean.FALSE;
    }
}
