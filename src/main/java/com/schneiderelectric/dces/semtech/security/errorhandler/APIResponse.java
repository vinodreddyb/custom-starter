package com.schneiderelectric.dces.semtech.security.errorhandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class APIResponse {
    private HttpStatus status;
    private Object body;
    private Error error;

    @Data
    @Builder
    public static class Error {
        private String message;
        private String errorMessage;
        private String errorCode;
        private String uri;
    }
}