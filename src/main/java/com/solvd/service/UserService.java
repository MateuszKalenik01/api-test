package com.solvd.service;

import com.solvd.model.Gender;
import com.solvd.model.Status;
import com.solvd.model.User;

import java.util.List;
import java.util.Random;

public class UserService {
    private static final List<String> NAMES = List.of("Ethan", "Oliver", "Liam", "Benjamin", "Lucas", "Henry",
            "Alexander", "Sebastian", "Jack", "Samuel", "Emily", "Sophia", "Amelia", "Emma", "Isabella",
            "Mia", "Ava", "Charlotte", "Harper", "Evelyn");

    private static final List<String> LASTNAMES = List.of("Clark", "Mitchell", "Turner", "Harris", "Carter",
            "Phillips", "Baker", "Evans", "Hall", "Roberts", "Campbell", "Parker", "Collins", "Stewart",
            "Sanchez", "Morris", "Reed", "Cook", "Morgan", "Bell");

    private static final List<String> DOMAIN = List.of("hotmail.com", "gmx.com", "icloud.com", "yandex.com",
            "fastmail.com", "posteo.net", "mailbox.org", "inbox.lv", "lavabit.com", "safe-mail.net");

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
