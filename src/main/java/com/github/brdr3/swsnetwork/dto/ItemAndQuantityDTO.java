package com.github.brdr3.swsnetwork.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ItemAndQuantityDTO {
    private Map<String, Integer> itemsAndQuantities;
}