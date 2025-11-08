package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProxySellerMonthlyRevenueRepository extends JpaRepository<ProxySellerMonthlyRevenue, Long> {
    @Query("""
        SELECT r FROM ProxySellerMonthlyRevenue r

        WHERE (:month IS NULL OR r.month = :month)
          AND (:year IS NULL OR r.year = :year)
        ORDER BY r.year DESC, r.month DESC
    """)
    Page<ProxySellerMonthlyRevenue> findAllByMonthAndYear(Integer month, Integer year, Pageable pageable);

    @Query("""
        SELECT DISTINCT r.year, r.month
        FROM ProxySellerMonthlyRevenue r
        ORDER BY r.year DESC, r.month DESC
    """)
    java.util.List<Object[]> findLatestMonthAndYear();

    @Query("SELECT MAX(r.month) FROM ProxySellerMonthlyRevenue r WHERE r.year = :year")
    Integer findLatestMonthByYear(Integer year);

    @Query("SELECT MAX(r.year) FROM ProxySellerMonthlyRevenue r WHERE r.month = :month")
    Integer findLatestYearByMonth(Integer month);

    @Query("""
        SELECT r FROM ProxySellerMonthlyRevenue r
        WHERE r.paymentStatus = :status
          AND r.paymentDueDate BETWEEN :start AND :end
        """)
    List<ProxySellerMonthlyRevenue> findPendingPaymentsDueBetween(
        LocalDateTime start, 
        LocalDateTime end, 
        PaymentStatus status
    );

    @Query("""
        SELECT r FROM ProxySellerMonthlyRevenue r
        WHERE r.paymentStatus = :status
          AND r.paymentDueDate < :now
        """)
    List<ProxySellerMonthlyRevenue> findOverduePayments(
        LocalDateTime now, 
        PaymentStatus status
    );

    @Query("""
        SELECT r FROM ProxySellerMonthlyRevenue r
        WHERE r.paymentStatus = :status
          AND r.paymentDueDate < :beforeDate
        """)
    List<ProxySellerMonthlyRevenue> findOverduePaymentsBefore(
        LocalDateTime beforeDate, 
        PaymentStatus status
    );
}
