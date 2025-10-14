package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.ApprovedDelegationRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovedDelegationRequestsRepository extends JpaRepository<ApprovedDelegationRequests, Long> {
}
