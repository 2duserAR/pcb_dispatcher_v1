package com.yadro.pcbdispatcher.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "pcbs")
@EntityListeners(AuditingEntityListener.class)
public class PCB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String serialNumber;
    
    @Enumerated(EnumType.STRING)
    private PCBStatus currentStatus;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
} 