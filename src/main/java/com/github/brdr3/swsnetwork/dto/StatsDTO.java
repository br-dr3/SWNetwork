package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class StatsDTO {
    private long rebels;
    private long betrayers;
    private Map<String, Float> resourcesPerRebel;
    private long corruptedPointsByBetrayers;
}
