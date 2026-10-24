package com.sauravjha.chathub.service;

import com.sauravjha.chathub.api.dto.MessageResponse;
import com.sauravjha.chathub.api.dto.SendMessageRequest;
import com.sauravjha.chathub.domain.Message;
import com.sauravjha.chathub.domain.MessageReceipt;
import com.sauravjha.chathub.domain.type.MessageType;
import com.sauravjha.chathub.domain.type.ReceiptState;
import com.sauravjha.chathub.exception.ConflictException;
import com.sauravjha.chathub.exception.ForbiddenException;
import com.sauravjha.chathub.exception.NotFoundException;
import com.sauravjha.chathub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MembershipService membershipService;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageReceiptRepository messageReceiptRepository;
    private final MessageReceiptStateService messageReceiptStateService;
    private final UserBlockRepository userBlockRepository;

    @Transactional
    public MessageResponse send(
            UUID senderId,
            UUID conversationId,
            SendMessageRequest request
    ){
        membershipService.requireMember(conversationId,senderId);

        var existing = messageRepository.findBySenderIdAndClientMessageId(senderId,request.clientMessageId());

        if(existing.isPresent()){
            var message = existing.get();

            // The same clientMessageId must not be reused for
            // different content and different convo

            if(!message.getConversationId().equals(conversationId) || !message.getContent().equals(request.content().strip())){
                throw new ConflictException("clientMessageId was already used for different content");
            }

            ReceiptState existingState = messageReceiptStateService.resolveForSender(message,senderId,
                    messageReceiptRepository.findAllByMessageId(message.getId())).orElseThrow(() -> new ForbiddenException("You are not allowed to view the state of message"));

            return toResponse(message,existingState);
        }

        var recipientIds = conversationMemberRepository.findRecipientIds(conversationId,senderId);

        if(recipientIds.isEmpty()){
            throw new NotFoundException("No recipients found for the sender");
        }

        var senderBlockedRecipientIds = userBlockRepository.findBlockedRecipientIds(senderId,recipientIds);

        var recipientsWhoBlockedSender = userBlockRepository.findRecipientsWhoBlockedSender(senderId,recipientIds);

        var blockedRecipientIds = new HashSet<>(senderBlockedRecipientIds);
        blockedRecipientIds.addAll(recipientsWhoBlockedSender);

        var deliverableRecipient =
                recipientIds.stream().filter((recipientId) -> !blockedRecipientIds.contains(recipientId)).toList();

        var conversation = conversationRepository.findByIdForUpdate(conversationId).orElseThrow(
                () -> new NotFoundException("Conversation not found")
        );

        long sequenceNumber = conversation.allocateNextMessageSequence();

        var message = new Message(conversationId,senderId,request.clientMessageId(),sequenceNumber, MessageType.TEXT,
                request.content().strip());

        messageRepository.save(message);

        if(!deliverableRecipient.isEmpty()){
            messageReceiptRepository.saveAll(
                    deliverableRecipient.stream()
                            .map(recipientId -> new MessageReceipt(message.getId(),recipientId)).toList()
            );
        }

        return toResponse(message,ReceiptState.SENT);
    }

    public static MessageResponse toResponse(Message message, ReceiptState state){
        return new MessageResponse(message.getId(),message.getConversationId(),message.getSenderId(),
                message.getClientMessageId(),message.getSequenceNumber(),message.getMessageType(),message.getContent(),
                message.getCreatedAt(),state);
    }
}
