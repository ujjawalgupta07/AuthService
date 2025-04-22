package com.demo.authService.dtos.response;

import com.demo.authService.dtos.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO {

    private ResponseStatus status;
}
