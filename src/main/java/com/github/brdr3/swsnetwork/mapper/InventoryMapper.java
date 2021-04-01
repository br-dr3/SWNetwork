package com.github.brdr3.swsnetwork.mapper;

import com.github.brdr3.swsnetwork.dal.entity.Item;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dto.ItemDTO;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryMapper {
    public static Item toItem(ItemDTO item, Rebel rebel) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .rebel(rebel)
                .build();
    }

    public static List<Item> toItemList(List<ItemDTO> inventory, Rebel rebel) {
        return inventory.stream()
                .map(itemDTO -> toItem(itemDTO, rebel))
                .collect(Collectors.toList());
    }

    public static ItemDTO toItemDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .build();
    }

    public static List<ItemDTO> toItemDTOList(List<Item> inventory) {
        return inventory.stream()
                .map(InventoryMapper::toItemDTO)
                .collect(Collectors.toList());
    }
}
