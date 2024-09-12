package com.solvd.service;

import com.solvd.model.Gender;
import com.solvd.model.Status;
import com.solvd.model.User;

import java.util.List;
import java.util.Random;

public class UserService {
    private static final List<String> NAMES = List.of("James", "Michael", "Robert", "John", "David", "William",
            "Richard", "Joseph", "Thomas", "Daniel", "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara",
            "Susan", "Jessica", "Karen", "Sarah");
    private static final List<String> LASTNAMES = List.of("Smith", "Johnson", "Williams", "Brown", "Jones", "Gracia",
            "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson",
            "Thomas", "Taylor", "Moore", "Jackson", "Martin");
    private static final List<String> DOMAIN = List.of("gmail.com", "outlook.com", "yahoo.com", "protonmail.com",
            "zoho.com", "hushmail.com", "mailfance.com", "tutanota.com", "thexyz.com", "runbox.com");

    public static User createUser() {
        Random random = new Random();

        String name = NAMES.get(random.nextInt(NAMES.size())) + " " + LASTNAMES.get(random.nextInt(LASTNAMES.size()));

        String emailName = name.replace(" ", "_").toLowerCase();
        String email = emailName + random.nextInt(100) + "@" + DOMAIN.get(random.nextInt(DOMAIN.size()));

        Gender gender = Gender.values()[random.nextInt(Gender.values().length)];
        Status status = Status.values()[random.nextInt(Status.values().length)];

        return User.builder()
                .name(name)
                .email(email)
                .gender(gender)
                .status(status)
                .build();
    }
}
