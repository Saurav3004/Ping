package com.sauravjha.chathub.service;

import com.sauravjha.chathub.api.dto.AuthResponse;
import com.sauravjha.chathub.api.dto.LoginRequest;
import com.sauravjha.chathub.api.dto.RegisterRequest;
import com.sauravjha.chathub.domain.UserAccount;
import com.sauravjha.chathub.exception.ConflictException;
import com.sauravjha.chathub.exception.ForbiddenException;
import com.sauravjha.chathub.exception.NotFoundException;
import com.sauravjha.chathub.repository.UserAccountRepository;
import com.sauravjha.chathub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request){
        if(userAccountRepository.existsByEmailIgnoreCase(request.email())){
            throw new ConflictException("An account already exists for this email");
        }

        var user = userAccountRepository.save(
                new UserAccount(request.displayName(),request.email(),passwordEncoder.encode(request.password()))
        );

        auditService.record(user.getId(),"USER_REGISTER","USER",user.getId().toString(), Map.of());

        return response(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request){
        var user =
                userAccountRepository.findByEmailIgnoreCase(request.email()).orElseThrow(() -> new ForbiddenException("Invalid email or password"));

        if(!user.isEnabled() || !passwordEncoder.matches(request.password(),user.getPasswordHash())){
            throw new ForbiddenException("Invalid email or password");
        }

        return response(user);
    }

    private AuthResponse response(UserAccount user){
        var token = jwtService.issue(user);
        return new AuthResponse(token.value(),"Bearer",token.expiresAt(),user.getId(),user.getDisplayName(),user.getEmail());
    }
}
