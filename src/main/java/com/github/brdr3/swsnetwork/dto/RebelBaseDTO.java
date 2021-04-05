package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class RebelBaseDTO {
    private UUID id;
    private String name;
    private Float latitude;
    private Float longitude;
}