package com.github.brdr3.swsnetwork.data.factory;

import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;

import java.util.Random;
import java.util.UUID;

public class RebelBaseFactory extends Factory {
    public RebelBase createRebelBase() {
        return RebelBase.builder()
                .id(UUID.randomUUID())
                .name(String.join(" ", faker.lorem().words()))
                .latitude(new Random().nextFloat())
                .longitude(new Random().nextFloat())
                .build();
    }

    public RebelBaseDTO createRebelBaseDTO() {
        return RebelBaseDTO.builder()
                .id(UUID.randomUUID())
                .name(String.join(" ", faker.lorem().words()))
                .latitude(new Random().nextFloat())
                .longitude(new Random().nextFloat())
                .build();
    }

    public RebelBaseDTO cloneIgnoringId(RebelBaseDTO other) {
        return RebelBaseDTO.builder()
                .id(null)
                .name(other.getName())
                .latitude(other.getLatitude())
                .longitude(other.getLongitude())
                .build();
    }
}
