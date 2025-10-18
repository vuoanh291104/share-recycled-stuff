package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.response.ProxySellerRevenueResponse;
import com.org.share_recycled_stuff.entity.ProxySellerMonthlyRevenue;
import com.org.share_recycled_stuff.repository.ProxySellerMonthlyRevenueRepository;
import com.org.share_recycled_stuff.service.ProxySellerRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProxySellerRevenueServiceImpl implements ProxySellerRevenueService {

    private final ProxySellerMonthlyRevenueRepository revenueRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProxySellerRevenueResponse> getRevenues(Integer month, Integer year, int page, int size) {
        if (month == null || year == null) {
            List<Object[]> latest = revenueRepository.findLatestMonthAndYear();
            if (!latest.isEmpty()) {
                Object[] first = latest.get(0);
                year = (Integer) first[0];
                month = (Integer) first[1];
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "year", "month"));
        Page<ProxySellerMonthlyRevenue> result = revenueRepository.findAllByMonthAndYear(month, year, pageable);

        return result.map(r -> ProxySellerRevenueResponse.builder()
                .name(r.getProxySeller().getUser().getFullName())
                .totalRevenue(r.getTotalSalesAmount())
                .discountProfitPayable(r.getAdminCommissionAmount())
                .paymentStatus(r.getPaymentStatus())
                .build());
    }
}
