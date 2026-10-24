package com.sauravjha.chathub.repository;

import com.sauravjha.chathub.domain.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserBlockRepository extends JpaRepository<UserBlock, UUID> {

    boolean existsByBlockerIdAndBlockedId(UUID blockerId,UUID blockedId);

    void deleteByBlockerIdAndBlockedId(UUID blockerId,UUID blockedId);

    @Query("""
        SELECT ub.blockedId FROM UserBlock ub
        WHERE ub.blockerId = :blockerId
        AND ub.blockedId IN :recipientIds
""")
    List<UUID> findBlockedRecipientIds(
            @Param("blockerId") UUID blockerId,
            @Param("recipientIds") Collection<UUID> recipientIds
    );

    @Query("""
        SELECT ub.blockerId FROM UserBlock ub
        WHERE ub.blockedId = :blockedId
        AND ub.blockerId IN :recipientIds
""")
    List<UUID> findRecipientsWhoBlockedSender(@Param("blockedId") UUID blockedId,@Param("recipientIds") Collection<UUID> recipientIds);
}
