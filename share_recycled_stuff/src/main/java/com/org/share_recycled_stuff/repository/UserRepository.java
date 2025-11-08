package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phoneNumber);
    @Query(value = """
        SELECT DISTINCT u FROM User u
        JOIN FETCH u.account a 
        LEFT JOIN FETCH a.roles ar
        WHERE
        a.isVerified = true
        AND a.isLocked = false
        AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:location IS NULL OR u.city = :location)
        AND (
            :isProxySeller IS NULL
            OR (
                :isProxySeller = true AND EXISTS (
                    SELECT 1 FROM UserRole ur
                    WHERE ur.account = a AND ur.roleType = com.org.share_recycled_stuff.entity.enums.Role.PROXY_SELLER
                )
            )
            OR (
                :isProxySeller = false AND NOT EXISTS (
                    SELECT 1 FROM UserRole ur2
                    WHERE ur2.account = a AND ur2.roleType = com.org.share_recycled_stuff.entity.enums.Role.PROXY_SELLER
                )
            )
        )
        ORDER BY u.ratingAverage DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT u) FROM User u
        JOIN u.account a
        WHERE
        a.isVerified = true
        AND a.isLocked = false
        AND (:keyword IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:location IS NULL OR u.city = :location)
        AND (
            :isProxySeller IS NULL
            OR (
                :isProxySeller = true AND EXISTS (
                    SELECT 1 FROM UserRole ur
                    WHERE ur.account = a AND ur.roleType = com.org.share_recycled_stuff.entity.enums.Role.PROXY_SELLER
                )
            )
            OR (
                :isProxySeller = false AND NOT EXISTS (
                    SELECT 1 FROM UserRole ur2
                    WHERE ur2.account = a AND ur2.roleType = com.org.share_recycled_stuff.entity.enums.Role.PROXY_SELLER
                )
            )
        )
        """)
    Page<User> searchUsers(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("isProxySeller") Boolean isProxySeller,
            Pageable pageable
    );
}
