package com.sauravjha.chathub.service;

import com.sauravjha.chathub.api.dto.AuthResponse;
import com.sauravjha.chathub.api.dto.RegisterRequest;
import com.sauravjha.chathub.repository.UserAccountRepository;
import com.sauravjha.chathub.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request){

    }
}
