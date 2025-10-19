package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}
