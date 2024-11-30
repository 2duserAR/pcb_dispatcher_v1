package com.yadro.pcbdispatcher.controller;

import com.yadro.pcbdispatcher.api.PCBApi;
import com.yadro.pcbdispatcher.config.ApiVersion;
import com.yadro.pcbdispatcher.dto.PCBCreateRequest;
import com.yadro.pcbdispatcher.model.PCB;
import com.yadro.pcbdispatcher.model.PCBHistory;
import com.yadro.pcbdispatcher.service.PCBService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiVersion.V1 + "/pcbs")
public class PCBController implements PCBApi {
    
    private final PCBService pcbService;

    public PCBController(PCBService pcbService) {
        this.pcbService = pcbService;
    }

    @Override
    public PCB createPCB(PCBCreateRequest request) {
        return pcbService.registerNewPCB(request.getSerialNumber());
    }

    @Override
    public PCB moveToNextStatus(Long id) {
        return pcbService.moveToNextStatus(id);
    }

    @Override
    public PCB moveToRepair(Long id) {
        return pcbService.moveToRepair(id);
    }

    @Override
    public List<PCBHistory> getPCBHistory(Long id) {
        return pcbService.getPCBHistory(id);
    }

    @Override
    public List<PCB> getAllPCBs() {
        return pcbService.getAllPCBs();
    }
} 