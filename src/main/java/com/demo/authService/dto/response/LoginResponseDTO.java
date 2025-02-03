package com.demo.authService.dto.response;

import com.demo.authService.dto.ResponseStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDTO {

    private ResponseStatus status;
}
