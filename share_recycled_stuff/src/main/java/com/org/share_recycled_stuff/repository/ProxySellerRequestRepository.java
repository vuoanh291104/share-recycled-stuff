package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.ProxySellerRequests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProxySellerRequestRepository extends JpaRepository<ProxySellerRequests, Long> {
}
