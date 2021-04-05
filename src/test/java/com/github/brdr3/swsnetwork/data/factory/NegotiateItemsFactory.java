package com.github.brdr3.swsnetwork.data.factory;

import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dto.NegotiateItemsDTO;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class NegotiateItemsFactory extends Factory {
    public NegotiateItemsDTO createNegotiateItemsDTO() {
        return NegotiateItemsDTO.builder()
                .firstRebel(UUID.randomUUID())
                .secondRebel(UUID.randomUUID())
                .firstRebelItems(EnumSet.allOf(Item.class)
                        .stream()
                        .map(e -> Map.entry(e.toString(), faker.random().nextInt(0, Integer.MAX_VALUE-1)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .secondRebelItems(EnumSet.allOf(Item.class)
                        .stream()
                        .map(e -> Map.entry(e.toString(), faker.random().nextInt(0, Integer.MAX_VALUE-1)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }
}
