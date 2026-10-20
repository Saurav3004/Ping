package com.sauravjha.chathub.service;

import com.sauravjha.chathub.api.dto.ReceiptRequest;
import com.sauravjha.chathub.api.dto.ReceiptResponse;
import com.sauravjha.chathub.exception.NotFoundException;
import com.sauravjha.chathub.repository.ConversationMemberRepository;
import com.sauravjha.chathub.repository.MessageReceiptRepository;
import com.sauravjha.chathub.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final MembershipService membershipService;
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final MessageReceiptRepository messageReceiptRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageReceiptStateService messageReceiptStateService;

    @Transactional
    public ReceiptResponse acknowledge(
            UUID userId,
            UUID conversationId,
            UUID messageId,
            ReceiptRequest request
    ){
        membershipService.requireMember(conversationId,userId);

        var message = messageRepository.findByIdAndConversationId(messageId,conversationId).orElseThrow(() -> new NotFoundException("Message not found"));

        var receipt =
                messageReceiptRepository.findByMessageIdAndUserId(messageId,userId).orElseThrow(() -> new NotFoundException("Receipt not found for this user"));

        var now = Instant.now();

        switch(request.type()){
            case DELIVERED -> receipt.markDelivered(now);
            case READ -> {
                receipt.markRead(now);

                var member = conversationMemberRepository.findByConversationIdAndUserId(conversationId,userId)
                        .orElseThrow(() -> new NotFoundException("Conversation Member not found"));

                member.markReadThrough(message.getSequenceNumber());

                conversationMemberRepository.save(member);
            }
        }
    }
}
