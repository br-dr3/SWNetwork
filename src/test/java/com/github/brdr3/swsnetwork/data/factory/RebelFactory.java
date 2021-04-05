package com.github.brdr3.swsnetwork.data.factory;

import com.github.brdr3.swsnetwork.dal.entity.Gender;
import com.github.brdr3.swsnetwork.dal.entity.Rebel;
import com.github.brdr3.swsnetwork.dto.RebelDTO;

import java.util.UUID;

public class RebelFactory extends Factory {
    private final RebelBaseFactory rebelBaseFactory = new RebelBaseFactory();
    private final InventoryFactory inventoryFactory = new InventoryFactory();

    public Rebel createRebel() {
        return Rebel.builder()
                .id(UUID.randomUUID())
                .betrayal(faker.random().nextBoolean())
                .rebelBase(rebelBaseFactory.createRebelBase())
                .birthDate(faker.date().birthday())
                .gender(faker.options().option(Gender.class))
                .name(faker.name().fullName())
                .inventory(inventoryFactory.createListItemPossession())
                .build();
    }

    public RebelDTO createRebelDTO() {
        return RebelDTO.builder()
                .id(UUID.randomUUID())
                .betrayal(faker.random().nextBoolean())
                .rebelBase(rebelBaseFactory.createRebelBaseDTO())
                .birthDate(faker.date().birthday())
                .gender(faker.options().option(Gender.class).toString())
                .name(faker.name().fullName())
                .inventory(inventoryFactory.createListItemPossessionDTO())
                .build();
    }

    public RebelDTO cloneIgnoringId(RebelDTO otherRebel) {
        return RebelDTO.builder()
                .id(null)
                .betrayal(otherRebel.isBetrayal())
                .rebelBase(otherRebel.getRebelBase())
                .birthDate(otherRebel.getBirthDate())
                .gender(otherRebel.getGender())
                .name(otherRebel.getName())
                .inventory(otherRebel.getInventory())
                .build();
    }
}
