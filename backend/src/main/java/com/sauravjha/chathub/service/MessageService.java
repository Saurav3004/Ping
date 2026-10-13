package com.sauravjha.chathub.service;

import com.sauravjha.chathub.api.dto.MessageResponse;
import com.sauravjha.chathub.api.dto.SendMessageRequest;
import com.sauravjha.chathub.domain.Message;
import com.sauravjha.chathub.domain.type.MessageType;
import com.sauravjha.chathub.exception.ConflictException;
import com.sauravjha.chathub.exception.NotFoundException;
import com.sauravjha.chathub.repository.ConversationRepository;
import com.sauravjha.chathub.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MembershipService membershipService;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

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

            return toResponse(message);
        }

        var conversation = conversationRepository.findByIdForUpdate(conversationId).orElseThrow(
                () -> new NotFoundException("Conversation not found")
        );

        long sequenceNumber = conversation.allocateNextMessageSequence();

        var message = new Message(conversationId,senderId,request.clientMessageId(),sequenceNumber, MessageType.TEXT,
                request.content().strip());

        messageRepository.save(message);

        return toResponse(message);
    }

    public static MessageResponse toResponse(Message message){
        return new MessageResponse(message.getId(),message.getConversationId(),message.getSenderId(),
                message.getClientMessageId(),message.getSequenceNumber(),message.getMessageType(),message.getContent(),
                message.getCreatedAt());
    }
}
