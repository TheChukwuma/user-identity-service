package com.octopus.user_identity_service.model;

import java.io.Serial;
import java.io.Serializable;

public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String descriptionOfAddress;
    private String longAddress;
    private String longitude;
    private String latitude;

}

