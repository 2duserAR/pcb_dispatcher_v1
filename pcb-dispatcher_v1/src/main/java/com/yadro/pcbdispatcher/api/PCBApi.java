package com.yadro.pcbdispatcher.api;

import com.yadro.pcbdispatcher.dto.PCBCreateRequest;
import com.yadro.pcbdispatcher.exception.ErrorResponse;
import com.yadro.pcbdispatcher.model.PCB;
import com.yadro.pcbdispatcher.model.PCBHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PCB Controller", description = "API для управления печатными платами")
public interface PCBApi {
    
    @Operation(summary = "Создание новой платы")
    @ApiResponse(responseCode = "200", description = "Плата успешно создана", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PCB.class),
                examples = @ExampleObject(value = PCBExamples.CREATE_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Неверный формат серийного номера", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = PCBExamples.INVALID_SERIAL)))
    @PostMapping
    PCB createPCB(@Valid @RequestBody PCBCreateRequest request);

    @Operation(summary = "Перемещение платы на следующий этап")
    @ApiResponse(responseCode = "200", description = "Плата успешно перемещена", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PCB.class),
                examples = @ExampleObject(value = PCBExamples.NEXT_STATUS_SUCCESS)))
    @ApiResponse(responseCode = "404", description = "Плата не найдена", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = PCBExamples.PCB_NOT_FOUND)))
    @PostMapping("/{id}/next")
    PCB moveToNextStatus(@PathVariable Long id);

    @Operation(summary = "Перемещение платы в ремонт")
    @ApiResponse(responseCode = "200", description = "Плата успешно отправлена в ремонт", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PCB.class),
                examples = @ExampleObject(value = PCBExamples.REPAIR_SUCCESS)))
    @ApiResponse(responseCode = "400", description = "Неверный текущий статус платы", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = PCBExamples.INVALID_STATUS_FOR_REPAIR)))
    @ApiResponse(responseCode = "404", description = "Плата не найдена", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = PCBExamples.PCB_NOT_FOUND)))
    @PostMapping("/{id}/repair")
    PCB moveToRepair(@PathVariable Long id);

    @Operation(summary = "Получение истории перемещений платы")
    @ApiResponse(responseCode = "200", description = "История успешно получена", 
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = PCBHistory.class)),
                examples = @ExampleObject(value = PCBExamples.HISTORY_SUCCESS)))
    @ApiResponse(responseCode = "404", description = "Плата не найдена", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = PCBExamples.PCB_NOT_FOUND)))
    @GetMapping("/{id}/history")
    List<PCBHistory> getPCBHistory(@PathVariable Long id);
} 