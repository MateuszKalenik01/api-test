package com.solvd.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private int id;
    private String name;
    private String email;
    private Gender gender;
    private Status status;
}
