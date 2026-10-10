package com.sauravjha.chathub.repository;

import com.sauravjha.chathub.api.dto.ConversationMemberResponse;
import com.sauravjha.chathub.api.dto.MemberResponse;
import com.sauravjha.chathub.domain.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {

    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId,UUID userId);

    @Query("""
        SELECT new com.sauravjha.chathub.api.dto.MemberResponse(
            cm.userId,u.displayName,cm.role,cm.lastReadSequence
        )
        FROM ConversationMember cm
            JOIN UserAccount u
                ON u.id = cm.userId
                    WHERE cm.conversationId = :conversationId
""")
    List<MemberResponse> findMemberResponses(
            @Param("conversationId") UUID conversationId
    );

    @Query("""
        SELECT new com.sauravjha.chathub.api.dto.ConversationMemberResponse(
            cm.conversationId,cm.userId,u.displayName,cm.role,cm.lastReadSequence
        )
        FROM ConversationMember cm
            JOIN UserAccount u
                ON u.id = cm.userId
                    WHERE cm.conversationId IN :conversationIds
""")
    List<ConversationMemberResponse> findConversationMemberResponses(
            @Param("conversationIds") Collection<UUID> conversationIds
    );

    boolean existsByConversationIdAndUserId(UUID conversationId,UUID userId);
}
