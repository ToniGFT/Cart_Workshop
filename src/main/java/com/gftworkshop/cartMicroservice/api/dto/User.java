package com.gftworkshop.cartMicroservice.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String email;
    private String name;
    private String lastName;
    private String password;
    private Integer fidelityPoints;
    private String birthDate;
    private String phoneNumber;
    private Country country;


}

