package com.yadro.pcbdispatcher.service;

import com.yadro.pcbdispatcher.model.PCB;
import com.yadro.pcbdispatcher.model.PCBHistory;
import com.yadro.pcbdispatcher.model.PCBStatus;
import com.yadro.pcbdispatcher.repository.PCBHistoryRepository;
import com.yadro.pcbdispatcher.repository.PCBRepository;
import com.yadro.pcbdispatcher.exception.PCBNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PCBService {
    private final PCBRepository pcbRepository;
    private final PCBHistoryRepository historyRepository;

    @Transactional
    public PCB registerNewPCB(String serialNumber) {
        log.info("Регистрация новой платы с серийным номером: {}", serialNumber);
        PCB pcb = new PCB();
        pcb.setSerialNumber(serialNumber);
        pcb.setCurrentStatus(PCBStatus.REGISTRATION);
        PCB savedPcb = pcbRepository.save(pcb);
        log.info("Плата зарегистрирована: {}", savedPcb);
        return savedPcb;
    }

    @Transactional
    public PCB moveToNextStatus(Long pcbId) {
        PCB pcb = getPCBById(pcbId);
        
        PCBStatus nextStatus = getNextStatus(pcb.getCurrentStatus());
        
        log.info("Перемещение платы {} из статуса {} в статус {}", 
                pcb.getSerialNumber(), 
                pcb.getCurrentStatus().getDescription(), 
                nextStatus.getDescription());
        
        PCBHistory history = new PCBHistory();
        history.setPcb(pcb);
        history.setFromStatus(pcb.getCurrentStatus());
        history.setToStatus(nextStatus);
        history.setTimestamp(LocalDateTime.now());
        
        pcb.setCurrentStatus(nextStatus);
        pcb.setUpdatedAt(LocalDateTime.now());
        
        historyRepository.save(history);
        return pcbRepository.save(pcb);
    }

    private PCBStatus getNextStatus(PCBStatus currentStatus) {
        return switch (currentStatus) {
            case REGISTRATION -> PCBStatus.COMPONENT_INSTALLATION;
            case COMPONENT_INSTALLATION -> PCBStatus.QUALITY_CONTROL;
            case QUALITY_CONTROL -> PCBStatus.PACKAGING;
            case REPAIR -> PCBStatus.QUALITY_CONTROL;
            case PACKAGING -> throw new IllegalStateException("Плата уже на последнем этапе");
        };
    }

    @Transactional
    public PCB moveToRepair(Long pcbId) {
        PCB pcb = getPCBById(pcbId);
        
        if (pcb.getCurrentStatus() != PCBStatus.QUALITY_CONTROL) {
            log.error("Попытка переместить плату {} в ремонт из неверного статуса: {}", 
                    pcb.getSerialNumber(), pcb.getCurrentStatus());
            throw new IllegalStateException("Перемещение в ремонт возможно только с этапа контроля качества");
        }
        
        log.info("Перемещение платы {} в ремонт", pcb.getSerialNumber());
        
        PCBHistory history = new PCBHistory();
        history.setPcb(pcb);
        history.setFromStatus(pcb.getCurrentStatus());
        history.setToStatus(PCBStatus.REPAIR);
        history.setTimestamp(LocalDateTime.now());
        
        pcb.setCurrentStatus(PCBStatus.REPAIR);
        pcb.setUpdatedAt(LocalDateTime.now());
        
        historyRepository.save(history);
        return pcbRepository.save(pcb);
    }

    public List<PCBHistory> getPCBHistory(Long pcbId) {
        pcbRepository.findById(pcbId)
            .orElseThrow(() -> new PCBNotFoundException(pcbId));
        
        return historyRepository.findByPcbIdOrderByTimestampDesc(pcbId);
    }

    public PCB getPCBById(Long id) {
        return pcbRepository.findById(id)
                .orElseThrow(() -> new PCBNotFoundException(id));
    }

    public List<PCB> getAllPCBs() {
        log.info("Получение списка всех плат");
        return pcbRepository.findAll();
    }
} 