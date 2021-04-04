package com.github.brdr3.swsnetwork.dal.entity;

import lombok.Getter;

@Getter
public enum Item {
    GUN("Gun", 4),
    AMMO("Ammo", 3),
    WATER("Water", 2),
    FOOD("Food", 1);

    private final String name;
    private final int value;

    Item(String n, int v) {
        this.name = n;
        this.value = v;
    }

    public static Item toItem(String name) {
        switch (name) {
            case "Gun":
                return Item.GUN;
            case "Ammo":
                return Item.AMMO;
            case "Food":
                return Item.FOOD;
            case "Water":
                return Item.WATER;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
