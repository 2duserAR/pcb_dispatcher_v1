package com.yadro.pcbdispatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yadro.pcbdispatcher.model.PCBHistory;
import java.util.List;

@Repository
public interface PCBHistoryRepository extends JpaRepository<PCBHistory, Long> {
    List<PCBHistory> findByPcbIdOrderByTimestampDesc(Long pcbId);
} 