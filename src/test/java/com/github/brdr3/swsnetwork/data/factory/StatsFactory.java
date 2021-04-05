package com.github.brdr3.swsnetwork.data.factory;

import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dto.StatsDTO;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsFactory extends Factory {
    public StatsDTO createStatsDTO() {
        return StatsDTO.builder()
                .betrayers(faker.random().nextLong())
                .rebels(faker.random().nextLong())
                .corruptedPointsByBetrayers(faker.random().nextLong())
                .resourcesPerRebel(EnumSet.allOf(Item.class)
                        .stream()
                        .map(e -> Map.entry(e.getName(), (float) faker.random().nextDouble()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                )
                .build();
    }
}
