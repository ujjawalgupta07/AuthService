package com.demo.authService.controller;

import com.demo.authService.dto.ResponseStatus;
import com.demo.authService.dto.request.LoginRequestDTO;
import com.demo.authService.dto.request.SignupRequestDTO;
import com.demo.authService.dto.response.LoginResponseDTO;
import com.demo.authService.dto.response.SignupResponseDTO;
import com.demo.authService.exceptions.UserNotFoundException;
import com.demo.authService.exceptions.WrongPasswordException;
import com.demo.authService.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign_up")
    public ResponseEntity<SignupResponseDTO> signUp(@RequestBody SignupRequestDTO signupRequestDTO) {
        SignupResponseDTO response = new SignupResponseDTO();

        try{
            if(authService.signup(signupRequestDTO.getEmail(), signupRequestDTO.getPassword())) {
                response.setStatus(ResponseStatus.SUCCESS);
            }
            else {
                response.setStatus(ResponseStatus.FAILURE);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e) {
            response.setStatus(ResponseStatus.FAILURE);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginDto = new LoginResponseDTO();
        ResponseEntity<LoginResponseDTO> response;
        try{
            String token = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            loginDto.setStatus(ResponseStatus.SUCCESS);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("AUTH_TOKEN", token);
             response = new ResponseEntity<>(
                    loginDto, headers, HttpStatus.OK
            );
        } catch (WrongPasswordException e) {
             response = new ResponseEntity<>(
                    loginDto, null, HttpStatus.CONFLICT
            );
        }
        catch (UserNotFoundException e) {
             response = new ResponseEntity<>(
                    loginDto, null, HttpStatus.NOT_FOUND
            );
        }

        return response;
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("token") String token) {
        return authService.validate(token);
    }
}
