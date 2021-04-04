package com.github.brdr3.swsnetwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NegotiateItemsDTO {
    private UUID firstRebel;
    private UUID secondRebel;
    private ItemAndQuantityDTO firstRebelItems;
    private ItemAndQuantityDTO secondRebelItems;
}
