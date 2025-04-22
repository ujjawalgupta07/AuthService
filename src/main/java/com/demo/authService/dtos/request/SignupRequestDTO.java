package com.demo.authService.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    private String email;
    private String password;
}
