package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.DelegationRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DelegationRequestsRepository extends JpaRepository<DelegationRequests, Long> {
    @Query("SELECT DISTINCT r FROM DelegationRequests r " +
            "LEFT JOIN FETCH r.images " +
            "LEFT JOIN FETCH r.customer c LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH r.proxySeller ps LEFT JOIN FETCH ps.user " +
            "WHERE r.proxySeller.id = :accountId")
    Page<DelegationRequests> findByProxySellerIdWithImages(@Param("accountId") Long accountId, Pageable pageable);

    @Query("SELECT DISTINCT r FROM DelegationRequests r " +
            "LEFT JOIN FETCH r.images " +
            "LEFT JOIN FETCH r.customer c LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH r.proxySeller ps LEFT JOIN FETCH ps.user " +
            "WHERE r.customer.id = :accountId")
    Page<DelegationRequests> findByCustomerIdWithImages(@Param("accountId") Long accountId, Pageable pageable);

    @Query("SELECT r FROM DelegationRequests r " +
            "LEFT JOIN FETCH r.images " +
            "LEFT JOIN FETCH r.customer c LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH r.proxySeller ps LEFT JOIN FETCH ps.user " +
            "WHERE r.id = :id")
    Optional<DelegationRequests> findByIdWithImages(@Param("id") Long id);

    Page<DelegationRequests> findByProxySellerId(Long accountId, Pageable pageable);
    Page<DelegationRequests> findByCustomerId(Long accountId, Pageable pageable);
}
