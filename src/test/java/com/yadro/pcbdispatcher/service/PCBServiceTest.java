package com.yadro.pcbdispatcher.service;

import com.yadro.pcbdispatcher.model.PCB;
import com.yadro.pcbdispatcher.model.PCBHistory;
import com.yadro.pcbdispatcher.model.PCBStatus;
import com.yadro.pcbdispatcher.exception.PCBNotFoundException;
import com.yadro.pcbdispatcher.repository.PCBHistoryRepository;
import com.yadro.pcbdispatcher.repository.PCBRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PCBServiceTest {

    @Mock
    private PCBRepository pcbRepository;

    @Mock
    private PCBHistoryRepository historyRepository;

    @InjectMocks
    private PCBService pcbService;

    @Test
    void registerNewPCB_ShouldCreatePCBWithRegistrationStatus() {
        // given
        String serialNumber = "TEST001";
        PCB expectedPCB = new PCB();
        expectedPCB.setSerialNumber(serialNumber);
        expectedPCB.setCurrentStatus(PCBStatus.REGISTRATION);
        
        when(pcbRepository.save(any(PCB.class))).thenReturn(expectedPCB);

        // when
        PCB result = pcbService.registerNewPCB(serialNumber);

        // then
        assertNotNull(result);
        assertEquals(serialNumber, result.getSerialNumber());
        assertEquals(PCBStatus.REGISTRATION, result.getCurrentStatus());
        verify(pcbRepository).save(any(PCB.class));
    }

    @Test
    void moveToNextStatus_ShouldUpdateStatusAndCreateHistory() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.REGISTRATION);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        when(pcbRepository.save(any(PCB.class))).thenReturn(pcb);
        
        // when
        PCB result = pcbService.moveToNextStatus(pcbId);
        
        // then
        assertEquals(PCBStatus.COMPONENT_INSTALLATION, result.getCurrentStatus());
        verify(historyRepository).save(any());
    }
    
    @Test
    void moveToNextStatus_ShouldThrowException_WhenPCBNotFound() {
        // Arrange
        when(pcbRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PCBNotFoundException.class, () -> {
            pcbService.moveToNextStatus(1L);
        });
    }
    
    @Test
    void moveToNextStatus_ShouldThrowException_WhenPCBInPackaging() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.PACKAGING);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        
        // when & then
        assertThrows(IllegalStateException.class, () -> pcbService.moveToNextStatus(pcbId));
    }
    
    @Test
    void moveToRepair_ShouldUpdateStatusAndCreateHistory() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.QUALITY_CONTROL);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        when(pcbRepository.save(any(PCB.class))).thenReturn(pcb);
        
        // when
        PCB result = pcbService.moveToRepair(pcbId);
        
        // then
        assertEquals(PCBStatus.REPAIR, result.getCurrentStatus());
        verify(historyRepository).save(any());
    }
    
    @Test
    void moveToRepair_ShouldThrowException_WhenNotInQualityControl() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.REGISTRATION);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        
        // when & then
        assertThrows(IllegalStateException.class, () -> pcbService.moveToRepair(pcbId));
    }

    @Test
    void getPCBHistory_ShouldReturnHistoryList() {
        // Arrange
        PCB pcb = new PCB();
        pcb.setId(1L);
        pcb.setSerialNumber("TEST001");
        
        PCBHistory history = new PCBHistory();
        history.setPcb(pcb);
        history.setFromStatus(PCBStatus.REGISTRATION);
        history.setToStatus(PCBStatus.COMPONENT_INSTALLATION);
        
        List<PCBHistory> historyList = Collections.singletonList(history);
        
        when(pcbRepository.findById(1L)).thenReturn(Optional.of(pcb));
        when(historyRepository.findByPcbIdOrderByTimestampDesc(1L)).thenReturn(historyList);
        
        // Act
        List<PCBHistory> result = pcbService.getPCBHistory(1L);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(PCBStatus.REGISTRATION, result.get(0).getFromStatus());
        assertEquals(PCBStatus.COMPONENT_INSTALLATION, result.get(0).getToStatus());
    }

    @Test
    void moveToNextStatus_FromComponentInstallation_ShouldMoveToQualityControl() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.COMPONENT_INSTALLATION);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        when(pcbRepository.save(any(PCB.class))).thenReturn(pcb);
        
        // when
        PCB result = pcbService.moveToNextStatus(pcbId);
        
        // then
        assertEquals(PCBStatus.QUALITY_CONTROL, result.getCurrentStatus());
        verify(historyRepository).save(any());
    }

    @Test
    void moveToNextStatus_FromQualityControl_ShouldMoveToPackaging() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.QUALITY_CONTROL);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        when(pcbRepository.save(any(PCB.class))).thenReturn(pcb);
        
        // when
        PCB result = pcbService.moveToNextStatus(pcbId);
        
        // then
        assertEquals(PCBStatus.PACKAGING, result.getCurrentStatus());
        verify(historyRepository).save(any());
    }

    @Test
    void moveToNextStatus_FromRepair_ShouldMoveToQualityControl() {
        // given
        Long pcbId = 1L;
        PCB pcb = new PCB();
        pcb.setId(pcbId);
        pcb.setCurrentStatus(PCBStatus.REPAIR);
        
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.of(pcb));
        when(pcbRepository.save(any(PCB.class))).thenReturn(pcb);
        
        // when
        PCB result = pcbService.moveToNextStatus(pcbId);
        
        // then
        assertEquals(PCBStatus.QUALITY_CONTROL, result.getCurrentStatus());
        verify(historyRepository).save(any());
    }

    @Test
    void getPCBHistory_ShouldThrowException_WhenPCBNotFound() {
        // given
        Long pcbId = 3L;
        when(pcbRepository.findById(pcbId)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(PCBNotFoundException.class, () -> pcbService.getPCBHistory(pcbId));
    }
} 