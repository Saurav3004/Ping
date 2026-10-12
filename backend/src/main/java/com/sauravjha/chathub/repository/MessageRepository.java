package com.sauravjha.chathub.repository;

import com.sauravjha.chathub.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
