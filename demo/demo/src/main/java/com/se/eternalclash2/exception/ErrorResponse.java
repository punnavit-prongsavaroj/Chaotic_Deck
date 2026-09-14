package com.se.eternalclash2.exception;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
@Data @Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;
    
    @Data @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}
