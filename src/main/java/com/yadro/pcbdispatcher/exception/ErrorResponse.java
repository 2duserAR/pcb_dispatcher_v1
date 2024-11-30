package com.yadro.pcbdispatcher.exception;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String message;
    private String status;
    private LocalDateTime timestamp;
} 