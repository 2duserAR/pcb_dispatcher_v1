package com.yadro.pcbdispatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.yadro.pcbdispatcher.model.PCB;

public interface PCBRepository extends JpaRepository<PCB, Long> {
} 