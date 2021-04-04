package com.github.brdr3.swsnetwork.mapper;

import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dal.entity.ItemPossession;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dto.ItemPossessionDTO;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapper {
    public static ItemPossession toItemPossession(ItemPossessionDTO itemPossessionDTO, Rebel rebel) {
        return ItemPossession.builder()
                .id(itemPossessionDTO.getId())
                .item(Item.toItem(itemPossessionDTO.getItem()))
                .quantity(itemPossessionDTO.getQuantity())
                .rebel(rebel)
                .build();
    }

    public static List<ItemPossession> toItemPossessionList(List<ItemPossessionDTO> inventory, Rebel rebel) {
        return inventory.stream()
                .map(itemPossessionDTO -> toItemPossession(itemPossessionDTO, rebel))
                .filter(itemPossessionDTO -> itemPossessionDTO.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    public static ItemPossessionDTO toItemPossessionDTO(ItemPossession itemPossession) {
        return ItemPossessionDTO.builder()
                .id(itemPossession.getId())
                .item(itemPossession.getItem().toString())
                .quantity(itemPossession.getQuantity())
                .build();
    }

    public static List<ItemPossessionDTO> toItemDTOList(List<ItemPossession> inventory) {
        return inventory.stream()
                .map(InventoryMapper::toItemPossessionDTO)
                .filter(itemPossession -> itemPossession.getQuantity() > 0)
                .collect(Collectors.toList());
    }
}
