package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<Account> findByVerificationToken(String token);
}
