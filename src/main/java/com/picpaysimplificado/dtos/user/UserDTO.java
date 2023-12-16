package com.picpaysimplificado.dtos.user;

import java.math.BigDecimal;

import com.picpaysimplificado.domain.user.UserType;

public record UserDTO(String firstName, String lastName, String document, BigDecimal balance, String email, String password, UserType userType) {
    
}
