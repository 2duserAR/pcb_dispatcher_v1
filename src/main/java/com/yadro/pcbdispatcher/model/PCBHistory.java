package com.yadro.pcbdispatcher.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "pcb_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PCBHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "pcb_id")
    private PCB pcb;
    
    @Enumerated(EnumType.STRING)
    private PCBStatus fromStatus;
    
    @Enumerated(EnumType.STRING)
    private PCBStatus toStatus;
    
    private LocalDateTime timestamp;
} 