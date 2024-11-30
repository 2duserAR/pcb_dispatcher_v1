package com.yadro.pcbdispatcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PCBCreateRequest {
    @NotBlank(message = "Серийный номер не может быть пустым")
    @Pattern(regexp = "^TEST\\d{3}$", message = "Серийный номер должен быть в формате TESTxxx, где xxx - три цифры")
    private String serialNumber;
} 