package com.github.brdr3.swsnetwork.dal.entity;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    NOT_DEFINED("Not defined");

    private final String name;

    Gender(String s) {
        this.name = s;
    }

    public static Gender toGender(String name) {
        switch (name) {
            case "Male":
                return Gender.MALE;
            case "Female":
                return Gender.FEMALE;
            case "Not defined":
                return Gender.NOT_DEFINED;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
