package com.sauravjha.chathub.api;

import com.sauravjha.chathub.api.dto.AddMemberRequest;
import com.sauravjha.chathub.api.dto.ConversationResponse;
import com.sauravjha.chathub.api.dto.CreateConversationRequest;
import com.sauravjha.chathub.api.dto.UpdateConversationRequest;
import com.sauravjha.chathub.security.CurrentUser;
import com.sauravjha.chathub.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final CurrentUser currentUser;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse created(
            Authentication authentication,
            @Valid @RequestBody CreateConversationRequest createConversationRequest
    ){
         return conversationService.create(currentUser.id(authentication),createConversationRequest);
    }

    @GetMapping("/{conversationId}")
    @ResponseStatus(HttpStatus.OK)
    public ConversationResponse get(Authentication authentication,@PathVariable("conversationId") UUID conversationId){
        return conversationService.get(currentUser.id(authentication),conversationId);
    }

    @GetMapping
    public List<ConversationResponse> list(Authentication authentication){
        return conversationService.list(currentUser.id(authentication));
    }

    @PostMapping("/{conversationId}/members")
    public ConversationResponse addMember(Authentication authentication, @PathVariable("conversationId") UUID conversationId,
                                         @Valid @RequestBody AddMemberRequest request){
        return conversationService.addMember(currentUser.id(authentication),conversationId,request);
    }

    @DeleteMapping("/{conversationId}/members/{userId}")
    public ConversationResponse removeMember(Authentication authentication,@PathVariable("conversationId") UUID conversationId,
                                             @PathVariable("userId") UUID userId){
        return conversationService.removeMember(currentUser.id(authentication),conversationId,userId);
    }

    @PatchMapping("/{conversationId}")
    public ConversationResponse updateConversation(Authentication authentication,
                                                   @PathVariable("conversationId") UUID conversationId,
                                                   @Valid @RequestBody UpdateConversationRequest request){
        return conversationService.update(currentUser.id(authentication),conversationId,request);
    }
}
