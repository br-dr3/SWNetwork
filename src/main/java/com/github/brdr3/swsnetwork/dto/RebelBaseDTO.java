package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class RebelBaseDTO {
    private UUID id;
    private float latitude;
    private float longitude;
}