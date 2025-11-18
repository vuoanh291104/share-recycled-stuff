package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.StatisticsFilterRequest;
import com.org.share_recycled_stuff.dto.response.*;
import com.org.share_recycled_stuff.entity.Account;
import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import com.org.share_recycled_stuff.entity.enums.PostStatus;
import com.org.share_recycled_stuff.repository.AccountRepository;
import com.org.share_recycled_stuff.repository.DelegationRequestsRepository;
import com.org.share_recycled_stuff.repository.PostRepository;
import com.org.share_recycled_stuff.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final PostRepository postRepository;
    private final DelegationRequestsRepository delegationRequestsRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public StatisticsReportResponse getStatistics(StatisticsFilterRequest filters) {

        Optional<Account> proxySellerAccount = filters.getProxySellerId() != null ?
                accountRepository.findById(filters.getProxySellerId()) :
                Optional.empty();

        PostStats postStats = getPostStats(proxySellerAccount, filters);
        DelegationStats delegationStats = getDelegationStats(proxySellerAccount, filters);
        SalesAndRevenueStats salesStats = getSalesAndRevenueStats(proxySellerAccount, filters);
        return StatisticsReportResponse.builder()
                .postStats(postStats)
                .delegationStats(delegationStats)
                .salesAndRevenueStats(salesStats)
                .build();
    }

    private PostStats getPostStats(Optional<Account> account, StatisticsFilterRequest filters) {

        Account accountFilter = account.orElse(null);
        Integer day = filters.getDay();
        Integer month = filters.getMonth();
        Integer year = filters.getYear();

        long total = postRepository.countFiltered(
                accountFilter, null, day, month, year
        );
        long active = postRepository.countFiltered(
                accountFilter, PostStatus.ACTIVE, day, month, year
        );
        long edit = postRepository.countFiltered(
                accountFilter, PostStatus.EDIT, day, month, year
        );
        long deleted = postRepository.countFiltered(
                accountFilter, PostStatus.DELETED, day, month, year
        );

        return PostStats.builder()
                .totalPosts(total)
                .activePosts(active)
                .editRequestPosts(edit)
                .deletedPosts(deleted)
                .build();
    }

    private DelegationStats getDelegationStats(Optional<Account> account, StatisticsFilterRequest filters) {

        Account accountFilter = account.orElse(null);
        Integer day = filters.getDay();
        Integer month = filters.getMonth();
        Integer year = filters.getYear();

        long total = delegationRequestsRepository.countFiltered(
                accountFilter, null, day, month, year
        );
        long pending = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.PENDING, day, month, year
        );
        long approved = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.APPROVED, day, month, year
        );
        long rejected = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.REJECTED, day, month, year
        );
        long inTransit = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.IN_TRANSIT, day, month, year
        );
        long productReceived = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.PRODUCT_RECEIVED, day, month, year
        );
        long selling = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.SELLING, day, month, year
        );
        long sold = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.SOLD, day, month, year
        );
        long paymentCompleted = delegationRequestsRepository.countFiltered(
                accountFilter, DelegationRequestsStatus.PAYMENT_COMPLETED, day, month, year
        );

        return DelegationStats.builder()
                .totalReceived(total)
                .pending(pending)
                .approved(approved)
                .rejected(rejected)
                .inTransit(inTransit)
                .productReceived(productReceived)
                .selling(selling)
                .sold(sold)
                .paymentCompleted(paymentCompleted)
                .build();
    }
    private SalesAndRevenueStats getSalesAndRevenueStats(Optional<Account> account, StatisticsFilterRequest filters) {

        Account accountFilter = account.orElse(null);
        Integer day = filters.getDay();
        Integer month = filters.getMonth();
        Integer year = filters.getYear();

        List<DelegationRequestsStatus> soldStatus = Arrays.asList(
                DelegationRequestsStatus.SOLD,
                DelegationRequestsStatus.PAYMENT_COMPLETED
        );

        long totalOrders = delegationRequestsRepository.countFilteredBySoldDate(
                accountFilter, soldStatus, day, month, year
        );

        BigDecimal totalSalesAmount = delegationRequestsRepository.sumRevenueFiltered(
                accountFilter, soldStatus, day, month, year
        );

        BigDecimal totalProxyCommission = delegationRequestsRepository.sumProfitFiltered(
                accountFilter, soldStatus, day, month, year
        );

        totalSalesAmount = (totalSalesAmount == null) ? BigDecimal.ZERO : totalSalesAmount;
        totalProxyCommission = (totalProxyCommission == null) ? BigDecimal.ZERO : totalProxyCommission;

        return SalesAndRevenueStats.builder()
                .totalCompletedOrders(totalOrders)
                .totalSalesAmount(totalSalesAmount)
                .totalProxyCommission(totalProxyCommission)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsComparisonResponse getPostComparison() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPeriodStart;
        LocalDateTime previousPeriodStart;
        LocalDateTime previousPeriodEnd;

        currentPeriodStart = now.withDayOfMonth(1).with(LocalTime.MIN);
        previousPeriodStart = currentPeriodStart.minusMonths(1);
        previousPeriodEnd = currentPeriodStart.minusNanos(1);

        long currentCount = postRepository.countByCreatedAtBetween(currentPeriodStart, now);
        long previousCount = postRepository.countByCreatedAtBetween(previousPeriodStart, previousPeriodEnd);

        double percentageChange = 0.0;
        if (previousCount > 0) {
            percentageChange = ((double) (currentCount - previousCount) / previousCount) * 100.0;
        } else if (currentCount > 0) {
            percentageChange = 100.0;
        }

        return StatisticsComparisonResponse.builder()
                .currentPeriodCount(currentCount)
                .previousPeriodCount(previousCount)
                .percentageChange(percentageChange)
                .build();
    }
}
