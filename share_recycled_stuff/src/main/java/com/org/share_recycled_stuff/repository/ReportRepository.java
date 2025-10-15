package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.Reports;
import com.org.share_recycled_stuff.entity.enums.ReportStatus;
import com.org.share_recycled_stuff.entity.enums.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Reports, Long> {

    @Query("""
            SELECT DISTINCT r FROM Reports r 
            LEFT JOIN FETCH r.reporter reporter
            LEFT JOIN FETCH reporter.user reporterUser
            LEFT JOIN FETCH r.reportedPost 
            LEFT JOIN FETCH r.reportedAccount reportedAcc
            LEFT JOIN FETCH reportedAcc.user reportedUser
            LEFT JOIN FETCH r.processedBy processedBy
            LEFT JOIN FETCH processedBy.user processedByUser
            WHERE (:reportType IS NULL OR r.reportType = :reportType)
            AND (:status IS NULL OR r.status = :status)
            AND (:reporterId IS NULL OR r.reporter.id = :reporterId)
            ORDER BY r.createdAt DESC""")
    Page<Reports> findAllWithFilters(
            @Param("reportType") ReportType reportType,
            @Param("status") ReportStatus status,
            @Param("reporterId") Long reporterId,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM Reports r 
            LEFT JOIN FETCH r.reporter reporter
            LEFT JOIN FETCH reporter.user reporterUser
            LEFT JOIN FETCH r.reportedPost post
            LEFT JOIN FETCH r.reportedAccount acc
            LEFT JOIN FETCH acc.user reportedUser
            LEFT JOIN FETCH r.processedBy processedBy
            LEFT JOIN FETCH processedBy.user processedByUser
            WHERE r.id = :reportId""")
    Optional<Reports> findByIdWithDetails(@Param("reportId") Long reportId);

    // Check if user already reported a post
    @Query("SELECT COUNT(r) > 0 FROM Reports r WHERE r.reporter.id = :reporterId AND r.reportedPost.id = :postId")
    boolean existsByReporterAndPost(@Param("reporterId") Long reporterId, @Param("postId") Long postId);

    // Check if user already reported an account
    @Query("SELECT COUNT(r) > 0 FROM Reports r WHERE r.reporter.id = :reporterId AND r.reportedAccount.id = :accountId")
    boolean existsByReporterAndAccount(@Param("reporterId") Long reporterId, @Param("accountId") Long accountId);

    // Statistics
    @Query("SELECT COUNT(r) FROM Reports r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReportStatus status);

    @Query("SELECT COUNT(r) FROM Reports r WHERE r.reportType = :reportType")
    Long countByReportType(@Param("reportType") ReportType reportType);

    @Query("SELECT COUNT(r) FROM Reports r WHERE r.createdAt >= :startDate AND r.createdAt < :endDate")
    Long countReportsInRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("""
            SELECT r FROM Reports r 
            WHERE r.reportedPost.id = :postId 
            ORDER BY r.createdAt DESC""")
    Page<Reports> findByReportedPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("""
            SELECT r FROM Reports r 
            WHERE r.reportedAccount.id = :accountId 
            ORDER BY r.createdAt DESC""")
    Page<Reports> findByReportedAccountId(@Param("accountId") Long accountId, Pageable pageable);
}

