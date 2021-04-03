package com.github.brdr3.swsnetwork.dal.repository;

import com.github.brdr3.swsnetwork.dal.entity.BetrayalReport;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BetrayalReportRepository extends JpaRepository<BetrayalReport, UUID> {
    @Query("SELECT br FROM BetrayalReport br WHERE br.reported = :rebel")
    List<BetrayalReport> getBetrayalReportsToRebel(Rebel rebel);
}
