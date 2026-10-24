package com.sauravjha.chathub.service;

import com.sauravjha.chathub.domain.UserBlock;
import com.sauravjha.chathub.exception.ConflictException;
import com.sauravjha.chathub.exception.NotFoundException;
import com.sauravjha.chathub.repository.UserAccountRepository;
import com.sauravjha.chathub.repository.UserBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSafetyService {

    private final UserAccountRepository userAccountRepository;
    private final UserBlockRepository userBlockRepository;
    private final AuditService auditService;

    @Transactional
    public void block(UUID blockerId,UUID blockedId){
        if(blockerId.equals(blockedId)){
            throw new ConflictException("A user cannot block themselves");
        }

        if(!userAccountRepository.existsById(blockedId)){
            throw new NotFoundException("User not found");
        }

        if(!userBlockRepository.existsByBlockerIdAndBlockedId(blockerId,blockedId)){
             userBlockRepository.save(new UserBlock(blockerId,blockedId));
        }

        auditService.record(
                blockedId,
                "USER_BLOCKED",
                "USER",
                blockedId.toString(),
                Map.of());
    }

    @Transactional(readOnly = true)
    public boolean isBlocked(UUID blockerId,UUID blockedId){
        return userBlockRepository.existsByBlockerIdAndBlockedId(blockerId,blockedId);
    }

    @Transactional
    public void unblock(UUID blockerId,UUID blockedId){
        if(userBlockRepository.existsByBlockerIdAndBlockedId(blockerId,blockedId)){
            userBlockRepository.deleteByBlockerIdAndBlockedId(blockerId,blockedId);
        }

        auditService.record(
                blockedId,
                "USER_UNBLOCKED",
                "USER",
                blockedId.toString(),
                Map.of()
        );
    }
}
