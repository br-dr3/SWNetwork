package com.github.brdr3.swsnetwork.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NegotiateItemsDTO {
    private UUID firstRebel;
    private UUID secondRebel;
    private Map<String, Integer> firstRebelItems;
    private Map<String, Integer> secondRebelItems;
}
