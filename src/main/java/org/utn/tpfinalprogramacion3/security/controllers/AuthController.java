package org.utn.tpfinalprogramacion3.security.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.utn.tpfinalprogramacion3.security.dto.AuthRequest;
import org.utn.tpfinalprogramacion3.security.dto.AuthResponse;
import org.utn.tpfinalprogramacion3.security.dto.RegisterRequest;
import org.utn.tpfinalprogramacion3.security.services.AuthService;
import org.utn.tpfinalprogramacion3.security.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping()
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest authRequest){
        UserDetails user = authService.authenticate(authRequest);
        System.out.println(user);
        String token = jwtService.generateToken(user);
        System.out.println(token);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

}

