package com.sauravjha.chathub.service;

import com.sauravjha.chathub.domain.ConversationMember;
import com.sauravjha.chathub.exception.ForbiddenException;
import com.sauravjha.chathub.repository.ConversationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MembershipService {
    private final ConversationMemberRepository cmRepo;

    public ConversationMember requireMember(UUID conversationId,UUID userId){
        return cmRepo.findByConversationIdAndUserId(conversationId,userId).orElseThrow(() -> new ForbiddenException("You are not a member of this conservation."));
    }

    public ConversationMember requireManager(UUID conversationId,UUID userId){
        var member = requireMember(conversationId,userId);

        if(!member.getRole().canManageMembers()){
            throw new ForbiddenException("This action requires owner or admin role.");
        }

        return member;
    }
}
