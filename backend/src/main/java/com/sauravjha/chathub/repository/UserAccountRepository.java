package com.sauravjha.chathub.repository;

import com.sauravjha.chathub.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount,UUID> {
}
