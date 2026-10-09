package com.sauravjha.chathub.service;

import com.sauravjha.chathub.api.dto.AddMemberRequest;
import com.sauravjha.chathub.api.dto.ConversationResponse;
import com.sauravjha.chathub.api.dto.CreateConversationRequest;
import com.sauravjha.chathub.api.dto.MemberResponse;
import com.sauravjha.chathub.domain.Conversation;
import com.sauravjha.chathub.domain.ConversationMember;
import com.sauravjha.chathub.domain.ConversationType;
import com.sauravjha.chathub.domain.MemberRole;
import com.sauravjha.chathub.exception.ConflictException;
import com.sauravjha.chathub.exception.ForbiddenException;
import com.sauravjha.chathub.exception.NotFoundException;
import com.sauravjha.chathub.repository.ConversationMemberRepository;
import com.sauravjha.chathub.repository.ConversationRepository;
import com.sauravjha.chathub.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final UserAccountRepository userAccountRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;

    private final MembershipService membershipService;

    @Transactional
    public ConversationResponse create(UUID creatorId, CreateConversationRequest request){
        var memberIds = new LinkedHashSet<>(request.memberIds());

        memberIds.add(creatorId);

        if(request.type() == ConversationType.DIRECT && memberIds.size() != 2){
            throw new ForbiddenException("A direct conversation must contain exactly two members.");
        }

        if(request.type() == ConversationType.GROUP && memberIds.size() < 3){
            throw new ForbiddenException("A group conversation must contain at least three members.");
        }

        if(request.type() == ConversationType.DIRECT){
            var existingConversationOptional = findDuplicateDirectConversation(memberIds);

            if(existingConversationOptional.isPresent()){
                return toResponse(existingConversationOptional.get());
            }
        }

        var users = userAccountRepository.findAllById(memberIds);

        if(users.size() != memberIds.size()){
            throw new NotFoundException("One or more conversation members do not exists");
        }

        var conversation = conversationRepository.save(
                new Conversation(request.type(),
                        request.type() == ConversationType.GROUP ? request.title() :  null,
                        creatorId)
        );

        var members = memberIds.stream().map(memberId -> new ConversationMember(
                conversation.getId(),
                memberId,
                memberId.equals(creatorId) ? MemberRole.OWNER : MemberRole.MEMBER
        )).toList();

        conversationMemberRepository.saveAll(members);

        return toResponse(conversation);

    }

    @Transactional(readOnly = true)
    public ConversationResponse get(
            UUID userId,
            UUID conversationId
    ){
        membershipService.requireMember(conversationId,userId);

        var conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new NotFoundException(
                "Conversation not found"));

        return toResponse(conversation);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> list(UUID userId){
        var conversations = conversationRepository.findAllForUser(userId);

        if(conversations.isEmpty()){
            return List.of();
        }

        var conversationIds = conversations.stream()
                .map((conversation) -> conversation.getId()).toList();

        var members = conversationMemberRepository.findConversationMemberResponses(conversationIds);

        var membersByConversation = members.stream()
                .collect(Collectors.groupingBy((conversationMemberResponse) -> conversationMemberResponse.conversationId()));

        return conversations.stream().map((conversation) -> {
            var conversationMembers = membersByConversation.getOrDefault(
                    conversation.getId(),
                    List.of()
            );

            var memberResponses = conversationMembers.stream()
                    .map((member) -> new MemberResponse(
                            member.userId(),
                            member.displayName(),
                            member.role(),
                            member.lastReadSequence()
                    )).toList();

            return new ConversationResponse(
                    conversation.getId(),
                    conversation.getType(),
                    conversation.getTitle(),
                    conversation.getCreatedBy(),
                    conversation.getCreatedAt(),
                    memberResponses
            );

        }).toList();
    }

    private Optional<Conversation> findDuplicateDirectConversation(
            LinkedHashSet<UUID> membersId
    ){
        return conversationRepository.findExistingDirectConversation(ConversationType.DIRECT,membersId);
    }

    @Transactional
    public ConversationResponse addMember(
            UUID actorId,
            UUID conversationId,
            AddMemberRequest request
    ){
        var conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new NotFoundException(
                "Conversation not found"));

        if(conversation.getType().equals(ConversationType.DIRECT)){
            throw new ConflictException(
                    "Create a group conversation instead of adding to a direct conversation."
            );
        }

        if(!userAccountRepository.existsById(request.userId()){
            throw new NotFoundException("User not found");
        }

//        if(request.)

        var actor = membershipService.requireManager(conversationId,actorId);


    }

    private ConversationResponse toResponse(Conversation conversation){
        return new ConversationResponse(conversation.getId(),conversation.getType(),conversation.getTitle(),
                conversation.getCreatedBy(),
                conversation.getCreatedAt(),conversationMemberRepository.findMemberResponses(conversation.getId()));
    }
}
