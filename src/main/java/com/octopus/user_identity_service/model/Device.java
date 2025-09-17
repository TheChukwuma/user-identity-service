package com.octopus.user_identity_service.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Device implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String type;
    private String os;
    private String ipAddress;
    private String macAddress;
    private LocalDateTime registeredAt;

}

