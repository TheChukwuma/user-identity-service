package com.octopus.user_identity_service.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity(name = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;


}
