package com.github.brdr3.swsnetwork.mapper;

import com.github.brdr3.swsnetwork.dal.entity.Gender;
import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dal.entity.RebelBase;
import com.github.brdr3.swsnetwork.dto.RebelBaseDTO;
import com.github.brdr3.swsnetwork.dto.RebelDTO;

import java.util.List;

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
                .inventory(InventoryMapper.toItemDTOList(rebel.getInventory()))
                .build();
    }

    public static RebelBase toRebelBase(RebelBaseDTO rebelBase) {
        return RebelBase.builder()
                .id(rebelBase.getId())
                .latitude(rebelBase.getLatitude())
                .longitude(rebelBase.getLongitude())
                .build();
    }

    public static Rebel toRebel(RebelDTO rebelDTO) {
        Rebel rebel = Rebel.builder()
                .id(rebelDTO.getId())
                .name(rebelDTO.getName())
                .birthDate(rebelDTO.getBirthDate())
                .gender(Gender.toGender(rebelDTO.getGender()))
                .rebelBase(toRebelBase(rebelDTO.getRebelBase()))
                .build();

        List<Item> inventory = InventoryMapper.toItemList(rebelDTO.getInventory(), rebel);
        rebel.setInventory(inventory);

        return rebel;
    }

}
