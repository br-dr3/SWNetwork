package com.github.brdr3.swsnetwork.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ReportBetrayalDTO {
    private UUID id;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private Date createdAt;

    private UUID reporter;
    private UUID reported;
}