package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;
import com.org.share_recycled_stuff.entity.ProxySellerRequests;
import com.org.share_recycled_stuff.entity.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProxySellerRequestRepository extends JpaRepository<ProxySellerRequests, Long> {
    @Query("""
            select new com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse(
                p.id, u.fullName, a.email, p.idCard, p.addressDetail, p.status, p.createdAt)
            from ProxySellerRequests p
            left join p.account a
            left join a.user u
            """)
    Page<UpgradeRequestResponse> findAllRequest(Pageable pageable);

    @Query("""
            select new com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse(
                p.id, u.fullName, a.email, p.idCard, p.addressDetail, p.status, p.createdAt)
            from ProxySellerRequests p 
            left join p.account a
            left join a.user u
            where (:status is null or p.status = :status)
            """)
    Page<UpgradeRequestResponse> findAllRequest(@Param("status") RequestStatus status, Pageable pageable);

    @Query("""
            select new com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse(
                p.id, u.fullName, a.email, p.idCard, p.addressDetail, p.status, p.createdAt)
            from ProxySellerRequests p 
            left join p.account a
            left join a.user u
            where lower(u.fullName) like lower(concat('%', :fullName, '%'))
            """)
    Page<UpgradeRequestResponse> findRequestsByFullName(@Param("fullName") String fullName, Pageable pageable);
}
