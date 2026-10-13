package com.sauravjha.chathub.api;

import com.sauravjha.chathub.api.dto.MessageResponse;
import com.sauravjha.chathub.api.dto.SendMessageRequest;
import com.sauravjha.chathub.security.CurrentUser;
import com.sauravjha.chathub.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final CurrentUser currentUser;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(Authentication authentication, @PathVariable UUID conversationId, @Valid @RequestBody SendMessageRequest request){
        return messageService.send(currentUser.id(authentication),conversationId,request);
    }
}
