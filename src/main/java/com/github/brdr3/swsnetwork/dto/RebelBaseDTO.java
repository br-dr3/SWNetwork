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
    private String name;
    private Float latitude;
    private Float longitude;
}