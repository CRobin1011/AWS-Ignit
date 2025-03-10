package com.ignit.internship.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ignit.internship.dto.DefaultResponse;
import com.ignit.internship.dto.ResponseReturn;
import com.ignit.internship.dto.auth.JwtTokenResponse;
import com.ignit.internship.dto.auth.UserLoginRequest;
import com.ignit.internship.dto.auth.UserRegisterRequest;
import com.ignit.internship.service.auth.AuthenticationService;
import com.ignit.internship.service.auth.JwtTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public final class AuthenticationController {
    
    private final AuthenticationService authenticationService;

    private final JwtTokenService jwtTokenService;

    public AuthenticationController(
        final AuthenticationService authenticationService, 
        final JwtTokenService jwtTokenService
    ) {
        this.authenticationService = authenticationService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<DefaultResponse<Object>> register(@RequestBody @Valid UserRegisterRequest register) {
        return ResponseReturn.ok(null);
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultResponse<JwtTokenResponse>> login(@RequestBody UserLoginRequest login) throws Exception {
        String token = jwtTokenService.buildToken(authenticationService.authenticate(login));
        return ResponseReturn.ok(new JwtTokenResponse(token));
    }

    @GetMapping("/verify")
    public ResponseEntity<DefaultResponse<JwtTokenResponse>> verify(@RequestParam String token) throws AuthenticationException {
        return ResponseReturn.ok(null);
    }
}
