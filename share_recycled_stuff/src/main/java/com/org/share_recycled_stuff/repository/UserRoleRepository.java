package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Set<UserRole> findByAccountId(Long accountId);
}
