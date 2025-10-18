package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.response.ProxySellerRevenueResponse;
import org.springframework.data.domain.Page;

public interface ProxySellerRevenueService {
    Page<ProxySellerRevenueResponse> getRevenues(Integer month, Integer year, int page, int size);
}
