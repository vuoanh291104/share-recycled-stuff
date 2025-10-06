package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.enums.Role;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Account> findByVerificationToken(String token);

    @Query("SELECT COUNT(DISTINCT a) FROM Account a JOIN a.roles r WHERE r.roleType = :roleType AND a.isLocked = :isLocked")
    @Lock(LockModeType.PESSIMISTIC_READ)
    long countByRolesRoleTypeAndStatus(@Param("roleType") Role roleType, @Param("isLocked") boolean isLocked);

    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.user u LEFT JOIN FETCH a.roles r " +
           "WHERE (:search IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:role IS NULL OR r.roleType = :role) " +
           "AND (:isLocked IS NULL OR a.isLocked = :isLocked)")
    Page<Account> findAllWithFilters(@Param("search") String search, 
                                      @Param("role") Role role, 
                                      @Param("isLocked") Boolean isLocked, 
                                      Pageable pageable);
}
