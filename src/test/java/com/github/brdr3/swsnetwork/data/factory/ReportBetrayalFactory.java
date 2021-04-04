package com.github.brdr3.swsnetwork.data.factory;

import com.github.brdr3.swsnetwork.dto.ReportBetrayalDTO;

import java.util.Date;
import java.util.UUID;

public class ReportBetrayalFactory extends Factory {
    public ReportBetrayalDTO createReportBetrayalDTO() {
        Date now = new Date();
        return ReportBetrayalDTO.builder()
                .id(UUID.randomUUID())
                .createdAt(faker.date().between(new Date(now.getTime() - 100000), new Date(now.getTime() + 100000)))
                .reporter(UUID.randomUUID())
                .reporter(UUID.randomUUID())
                .build();
    }

    public ReportBetrayalDTO cloneIgnoringId(ReportBetrayalDTO otherReport) {
        return ReportBetrayalDTO.builder()
                .id(null)
                .createdAt(otherReport.getCreatedAt())
                .reporter(otherReport.getReporter())
                .reporter(otherReport.getReported())
                .build();
    }
}
