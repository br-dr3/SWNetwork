package com.github.brdr3.swsnetwork.data.factory;

import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dal.entity.ItemPossession;
import com.github.brdr3.swsnetwork.dto.ItemPossessionDTO;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class InventoryFactory {
    public List<ItemPossession> createListItemPossession() {
        return EnumSet.allOf(Item.class)
                .stream()
                .map(e -> ItemPossession.builder()
                        .item(e)
                        .quantity(new Random().nextInt())
                        .id(UUID.randomUUID())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ItemPossessionDTO> createListItemPossessionDTO() {
        return EnumSet.allOf(Item.class)
                .stream()
                .map(e -> ItemPossessionDTO.builder()
                        .item(e.toString())
                        .quantity(new Random().nextInt(10))
                        .id(UUID.randomUUID())
                        .build())
                .collect(Collectors.toList());
    }
}
