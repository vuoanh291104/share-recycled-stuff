package com.org.share_recycled_stuff.repository;

import com.org.share_recycled_stuff.entity.DelegationImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DelegationImagesRepository extends JpaRepository<DelegationImages, Integer> {
    List<DelegationImages> findByDelegationRequestId(Long delegationRequestId);
}
