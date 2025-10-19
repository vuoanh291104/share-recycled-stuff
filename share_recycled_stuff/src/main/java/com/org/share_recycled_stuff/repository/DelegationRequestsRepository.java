package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.DelegationRequests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DelegationRequestsRepository extends JpaRepository<DelegationRequests, Long> {
    @EntityGraph(attributePaths = {"customer", "proxySeller", "images"})
    Page<DelegationRequests> findByCustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "proxySeller", "images"})
    Page<DelegationRequests> findByProxySellerId(Long proxySellerId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "proxySeller", "images"})
    Optional<DelegationRequests> findById(Long id);
}
