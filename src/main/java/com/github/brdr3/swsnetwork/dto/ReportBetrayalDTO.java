package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ReportBetrayalDTO {
    private UUID id;
    private Date createdAt;
    private UUID reporter;
    private UUID reported;
}