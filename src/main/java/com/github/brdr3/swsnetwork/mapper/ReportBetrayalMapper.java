package com.github.brdr3.swsnetwork.mapper;

import com.github.brdr3.swsnetwork.dal.entity.BetrayalReport;
import com.github.brdr3.swsnetwork.dto.RebelDTO;
import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;

import java.util.Date;

public class ReportBetrayalMapper {
    public static BetrayalReport toBetrayalReport(ReportBetrayalDTO rb, RebelDTO reporter, RebelDTO reported) {
        Date createdAt = rb.getCreatedAt();
        return BetrayalReport.builder()
                             .id(rb.getId())
                             .createdAt(createdAt == null ? new Date() : createdAt)
                             .reporter(RebelMapper.toRebel(reporter))
                             .reported(RebelMapper.toRebel(reported))
                             .build();
    }

    public static ReportBetrayalDTO toReportBetrayalDTO(BetrayalReport br) {
        return ReportBetrayalDTO.builder()
                                .id(br.getId())
                                .createdAt(br.getCreatedAt())
                                .reporter(br.getReporter().getId())
                                .reported(br.getReported().getId())
                                .build();
    }
}
