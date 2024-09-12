package com.solvd.model;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Gender fromString(String genderString) {
        for (Gender gender : Gender.values()) {
            if (gender.value.equalsIgnoreCase(genderString)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender value: " + genderString);
    }
}