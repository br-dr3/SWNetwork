package com.github.brdr3.swsnetwork.mapper;

import com.github.brdr3.swsnetwork.dal.entity.Gender;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;

public class RebelMapper {
    public static RebelBaseDTO toRebelBaseDTO(RebelBase rebelBase) {
        return RebelBaseDTO.builder()
                .id(rebelBase.getId())
                .latitude(rebelBase.getLatitude())
                .longitude(rebelBase.getLongitude())
                .build();
    }

    public static RebelDTO toRebelDTO(Rebel rebel) {
        return RebelDTO.builder()
                .id(rebel.getId())
                .name(rebel.getName())
                .birthDate(rebel.getBirthDate())
                .gender(rebel.getGender().toString())
                .rebelBase(toRebelBaseDTO(rebel.getRebelBase()))
                .build();
    }

    public static RebelBase toRebelBase(RebelBaseDTO rebelBase) {
        return RebelBase.builder()
                .id(rebelBase.getId())
                .latitude(rebelBase.getLatitude())
                .longitude(rebelBase.getLongitude())
                .build();
    }

    public static Rebel toRebel(RebelDTO rebel) {
        return Rebel.builder()
                .id(null)
                .name(rebel.getName())
                .birthDate(rebel.getBirthDate())
                .gender(Gender.toGender(rebel.getGender()))
                .rebelBase(toRebelBase(rebel.getRebelBase()))
                .build();
    }

}
