package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.DelegationRequests;
import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("SELECT COUNT(d) FROM DelegationRequests d " +
            "WHERE (:account IS NULL OR d.proxySeller = :account) " +
            "AND (:status IS NULL OR d.status = :status) " +
            "AND (:day IS NULL OR DAY(d.createdAt) = :day) " +
            "AND (:month IS NULL OR MONTH(d.createdAt) = :month) " +
            "AND (:year IS NULL OR YEAR(d.createdAt) = :year)")
    long countFiltered(@Param("account") Account account,
                       @Param("status") DelegationRequestsStatus status,
                       @Param("day") Integer day,
                       @Param("month") Integer month,
                       @Param("year") Integer year);

    @Query("SELECT SUM(d.soldPrice) FROM DelegationRequests d " +
            "WHERE (:account IS NULL OR d.proxySeller = :account) " +
            "AND (:status IS NULL OR d.status = :status) " +
            "AND (:day IS NULL OR DAY(d.soldDate) = :day) " +
            "AND (:month IS NULL OR MONTH(d.soldDate) = :month) " +
            "AND (:year IS NULL OR YEAR(d.soldDate) = :year)")
    BigDecimal sumRevenueFiltered(@Param("account") Account account,
                                  @Param("status") DelegationRequestsStatus status,
                                  @Param("day") Integer day,
                                  @Param("month") Integer month,
                                  @Param("year") Integer year);

    @Query("SELECT SUM(d.commissionFee) FROM DelegationRequests d " +
            "WHERE (:account IS NULL OR d.proxySeller = :account) " +
            "AND (:status IS NULL OR d.status = :status) " +
            "AND (:day IS NULL OR DAY(d.soldDate) = :day) " +
            "AND (:month IS NULL OR MONTH(d.soldDate) = :month) " +
            "AND (:year IS NULL OR YEAR(d.soldDate) = :year)")
    BigDecimal sumProfitFiltered(@Param("account") Account account,
                                 @Param("status") DelegationRequestsStatus status,
                                 @Param("day") Integer day,
                                 @Param("month") Integer month,
                                 @Param("year") Integer year);

    @Query("SELECT COUNT(d) FROM DelegationRequests d " +
            "WHERE (:account IS NULL OR d.proxySeller = :account) " +
            "AND (:status IS NULL OR d.status = :status) " +
            "AND (:day IS NULL OR DAY(d.soldDate) = :day) " +
            "AND (:month IS NULL OR MONTH(d.soldDate) = :month) " +
            "AND (:year IS NULL OR YEAR(d.soldDate) = :year)")
    long countFilteredBySoldDate(@Param("account") Account account,
                                 @Param("status") DelegationRequestsStatus status,
                                 @Param("day") Integer day,
                                 @Param("month") Integer month,
                                 @Param("year") Integer year);
}
