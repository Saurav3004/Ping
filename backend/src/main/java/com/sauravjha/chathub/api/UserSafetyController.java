package com.sauravjha.chathub.api;

import com.sauravjha.chathub.api.dto.BlockStatusResponse;
import com.sauravjha.chathub.api.dto.BlockUserRequest;
import com.sauravjha.chathub.security.CurrentUser;
import com.sauravjha.chathub.service.UserSafetyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/safety")
@RequiredArgsConstructor
public class UserSafetyController {

    private final UserSafetyService userSafetyService;
    private final CurrentUser currentUser;

    @PostMapping("/blocks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(Authentication authentication,@Valid @RequestBody BlockUserRequest blockUserRequest){
        userSafetyService.block(currentUser.id(authentication),blockUserRequest.userId());
    }

    @GetMapping("/blocks/{userId}")
    public BlockStatusResponse blockStatus(Authentication authentication, @Valid @PathVariable UUID userId){
        return new BlockStatusResponse(
                userSafetyService.isBlocked(currentUser.id(authentication),userId)
        );
    }

    @DeleteMapping("/blocks/{blockedId}")
    public void unblock(Authentication authentication,@Valid @PathVariable UUID blockedId){
        userSafetyService.unblock(currentUser.id(authentication),blockedId);
    }
}
