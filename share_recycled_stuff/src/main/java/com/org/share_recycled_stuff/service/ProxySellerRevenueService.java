package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.response.ProxySellerRevenueResponse;
import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProxySellerRevenueService {
    Page<ProxySellerRevenueResponse> getRevenues(Integer month, Integer year, int page, int size);
}
