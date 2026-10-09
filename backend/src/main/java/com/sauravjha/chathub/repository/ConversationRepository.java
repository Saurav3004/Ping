package com.sauravjha.chathub.repository;

import com.sauravjha.chathub.domain.Conversation;
import com.sauravjha.chathub.domain.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
        SELECT c FROM Conversation c
        JOIN ConversationMember cm
            ON cm.conversationId = c.id
            WHERE c.type = :type
            AND cm.userId IN :memberIds
            GROUP BY c.id
            HAVING COUNT(DISTINCT cm.userId) = 2
""")
    Optional<Conversation> findExistingDirectConversation(
            @Param("type")ConversationType type,
            @Param("memberIds")Set<UUID> memberIds
            );

    @Query("""
         SELECT c FROM Conversation c JOIN ConversationMember cm
            ON cm.conversationId = c.id
               WHERE cm.userId = :userId
               ORDER BY cm.joinedAt DESC
""")
    List<Conversation> findAllForUser(@Param("userId") UUID userId);
}
