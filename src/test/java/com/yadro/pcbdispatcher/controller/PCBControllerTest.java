package com.yadro.pcbdispatcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.yadro.pcbdispatcher.dto.PCBCreateRequest;
import com.yadro.pcbdispatcher.model.PCB;
import com.yadro.pcbdispatcher.model.PCBHistory;
import com.yadro.pcbdispatcher.model.PCBStatus;
import com.yadro.pcbdispatcher.service.PCBService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PCBController.class)
class PCBControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PCBService pcbService;

    @Test
    void registerPCB_ShouldReturnCreatedPCB() throws Exception {
        PCB pcb = new PCB();
        pcb.setId(1L);
        pcb.setSerialNumber("TEST001");
        pcb.setCurrentStatus(PCBStatus.REGISTRATION);
        
        PCBCreateRequest request = new PCBCreateRequest();
        request.setSerialNumber("TEST001");
        
        when(pcbService.registerNewPCB("TEST001")).thenReturn(pcb);
        
        mockMvc.perform(post("/api/v1/pcbs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.serialNumber").value("TEST001"))
                .andExpect(jsonPath("$.currentStatus").value("REGISTRATION"));
    }

    @Test
    void moveToNextStatus_ShouldReturnUpdatedPCB() throws Exception {
        PCB pcb = new PCB();
        pcb.setId(1L);
        pcb.setSerialNumber("TEST001");
        pcb.setCurrentStatus(PCBStatus.COMPONENT_INSTALLATION);
        
        when(pcbService.moveToNextStatus(1L)).thenReturn(pcb);
        
        mockMvc.perform(post("/api/v1/pcbs/1/next")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("COMPONENT_INSTALLATION"));
    }

    @Test
    void moveToRepair_ShouldReturnUpdatedPCB() throws Exception {
        PCB pcb = new PCB();
        pcb.setId(1L);
        pcb.setSerialNumber("TEST001");
        pcb.setCurrentStatus(PCBStatus.REPAIR);
        
        when(pcbService.moveToRepair(1L)).thenReturn(pcb);
        
        mockMvc.perform(post("/api/v1/pcbs/1/repair")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("REPAIR"));
    }

    @Test
    void getPCBHistory_ShouldReturnHistoryList() throws Exception {
        PCB pcb = new PCB();
        pcb.setId(1L);
        pcb.setSerialNumber("TEST001");
        
        PCBHistory history = new PCBHistory();
        history.setPcb(pcb);
        history.setFromStatus(PCBStatus.REGISTRATION);
        history.setToStatus(PCBStatus.COMPONENT_INSTALLATION);
        
        when(pcbService.getPCBHistory(1L)).thenReturn(Collections.singletonList(history));
        
        mockMvc.perform(get("/api/v1/pcbs/1/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fromStatus").value("REGISTRATION"))
                .andExpect(jsonPath("$[0].toStatus").value("COMPONENT_INSTALLATION"));
    }

    @Test
    void registerPCB_ShouldReturnBadRequest_WhenSerialNumberInvalid() throws Exception {
        // Arrange
        PCBCreateRequest request = new PCBCreateRequest();
        request.setSerialNumber("INVALID");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/pcbs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Серийный номер должен быть в формате TESTxxx, где xxx - три цифры"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void givenEmptySerialNumber_whenRegisterPCB_thenReturnBadRequest() throws Exception {
        PCBCreateRequest request = new PCBCreateRequest();
        request.setSerialNumber("");
        
        MvcResult result = mockMvc.perform(post("/api/v1/pcbs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        String message = JsonPath.read(responseContent, "$.message");
        
        assertTrue(message.contains("Серийный номер не может быть пустым"), 
                "Сообщение должно содержать ошибку о пустом серийном номере");
        assertTrue(message.contains("Серийный номер должен быть в формате TESTxxx, где xxx - три цифры"), 
                "Сообщение должно содержать ошибку о формате серийного номера");
    }

    @Test
    void getAllPCBs_ShouldReturnListOfPCBs() throws Exception {
        PCB pcb1 = new PCB();
        pcb1.setId(1L);
        pcb1.setSerialNumber("TEST001");
        pcb1.setCurrentStatus(PCBStatus.REGISTRATION);
        
        PCB pcb2 = new PCB();
        pcb2.setId(2L);
        pcb2.setSerialNumber("TEST002");
        pcb2.setCurrentStatus(PCBStatus.COMPONENT_INSTALLATION);
        
        List<PCB> pcbs = List.of(pcb1, pcb2);
        
        when(pcbService.getAllPCBs()).thenReturn(pcbs);
        
        mockMvc.perform(get("/api/v1/pcbs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].serialNumber").value("TEST001"))
                .andExpect(jsonPath("$[0].currentStatus").value("REGISTRATION"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].serialNumber").value("TEST002"))
                .andExpect(jsonPath("$[1].currentStatus").value("COMPONENT_INSTALLATION"));
    }
} 