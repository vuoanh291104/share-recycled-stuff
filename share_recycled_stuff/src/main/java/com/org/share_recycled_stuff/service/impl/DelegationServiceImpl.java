package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.request.DelegationRequest;
import com.org.share_recycled_stuff.dto.response.DelegationResponse;
import com.org.share_recycled_stuff.dto.response.ProxySellerInfoResponse;
import com.org.share_recycled_stuff.entity.*;
import com.org.share_recycled_stuff.entity.enums.DelegationRequestsStatus;
import com.org.share_recycled_stuff.entity.enums.PaymentStatus;
import com.org.share_recycled_stuff.exception.AppException;
import com.org.share_recycled_stuff.exception.ErrorCode;
import com.org.share_recycled_stuff.mapper.DelegationMapper;
import com.org.share_recycled_stuff.repository.*;
import com.org.share_recycled_stuff.service.DelegationService;
import com.org.share_recycled_stuff.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DelegationServiceImpl implements DelegationService {

    private final DelegationRequestsRepository delegationRequestsRepository;
    private final AccountRepository accountRepository;
    private final DelegationImagesRepository delegationImagesRepository;
    private final ApprovedDelegationRequestsRepository approvedDelegationRequestsRepository;
    private final NotificationService notificationService;
    private final ProxySellerMonthlyRevenueRepository revenueRepository;

    @Override
    public DelegationResponse createDelegationRequest(DelegationRequest request, Long customerId) {
        Account customer = accountRepository.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        Account proxySeller = accountRepository.findById(request.getProxySellerId())
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        DelegationRequests entity = DelegationRequests.builder()
                .customer(customer)
                .proxySeller(proxySeller)
                .productDescription(request.getProductDescription())
                .expectPrice(request.getExpectPrice())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .commissionRate(new BigDecimal("10.00"))
                .status(DelegationRequestsStatus.PENDING)
                .build();

        DelegationRequests saved = delegationRequestsRepository.save(entity);

        Set<String> imageUrls = request.getImageUrls() != null ? request.getImageUrls() : Collections.emptySet();

        if (!imageUrls.isEmpty()) {
            java.util.List<DelegationImages> imagesToSave = new java.util.ArrayList<>();
            int order = 1;
            for (String url : imageUrls) {
                DelegationImages img = new DelegationImages();
                img.setDelegationRequest(saved);
                img.setImageUrl(url);
                img.setDisplayOrder(order++);
                imagesToSave.add(img);
            }
            delegationImagesRepository.saveAll(imagesToSave);
        }

        return DelegationResponse.builder()
                .id(saved.getId())
                .customerId(saved.getCustomer().getId())
                .customerName(saved.getCustomer().getUser().getFullName())
                .proxySellerId(saved.getProxySeller().getId())
                .proxySellerName(saved.getProxySeller().getUser().getFullName())
                .productDescription(saved.getProductDescription())
                .expectPrice(saved.getExpectPrice())
                .status(saved.getStatus().name())
                .rejectionReason(saved.getRejectionReason())
                .bankAccountNumber(saved.getBankAccountNumber())
                .bankName(saved.getBankName())
                .accountHolderName(saved.getAccountHolderName())
                .imageUrls(imageUrls)
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
    @Override
    public void approve(Long delegationId, Long proxySellerId, String note) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));

        if (request.getStatus() != DelegationRequestsStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Yêu cầu không ở trạng thái chờ duyệt");
        }

        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền duyệt yêu cầu này");
        }

        request.setStatus(DelegationRequestsStatus.APPROVED);
        delegationRequestsRepository.save(request);

        ApprovedDelegationRequests approved = ApprovedDelegationRequests.builder()
                .delegationRequest(request)
                .approvedBy(request.getProxySeller())
                .note(note)
                .build();

        approvedDelegationRequestsRepository.save(approved);

        String approvalMessage = note != null && !note.trim().isEmpty()
                ? String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã được chấp nhận. Ghi chú: %s",
                request.getProductDescription(), note)
                : String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã được chấp nhận.",
                request.getProductDescription());

        notificationService.createNotification(
                request.getCustomer().getId(),
                "Yêu cầu ký gửi được chấp nhận",
                approvalMessage,
                11,
                3,
                "DelegationRequest",
                delegationId
        );
    }

    @Override
    public void reject(Long requestId, Long proxySellerId, String reason) {
        DelegationRequests request = delegationRequestsRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST));
        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (request.getStatus() != DelegationRequestsStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        request.setStatus(DelegationRequestsStatus.REJECTED);
        request.setRejectionReason(reason);
        delegationRequestsRepository.save(request);

        String rejectionMessage = reason != null && !reason.trim().isEmpty()
                ? String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã bị từ chối. Lý do: %s",
                request.getProductDescription(), reason)
                : String.format("Yêu cầu ký gửi sản phẩm \"%s\" của bạn đã bị từ chối.",
                request.getProductDescription());

        notificationService.createNotification(
                request.getCustomer().getId(),
                "Yêu cầu ký gửi bị từ chối",
                rejectionMessage,
                12,
                3,
                "DelegationRequest",
                requestId
        );
    }
    @Transactional(readOnly = true)
    @Override
    public Page<DelegationResponse> getDelegationRequests(Long accountId, String role, Pageable pageable) {
        Page<DelegationRequests> page;

        if ("PROXY_SELLER".equalsIgnoreCase(role)) {
            page = delegationRequestsRepository.findByProxySellerIdWithImages(accountId, pageable);
        } else if ("CUSTOMER".equalsIgnoreCase(role)) {
            page = delegationRequestsRepository.findByCustomerIdWithImages(accountId, pageable);
        } else {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return page.map(req -> {
            Set<String> imageUrls = req.getImages() != null
                    ? req.getImages().stream()
                    .map(DelegationImages::getImageUrl)
                    .collect(Collectors.toSet())
                    : Collections.emptySet();

            return DelegationResponse.builder()
                    .id(req.getId())
                    .customerId(req.getCustomer().getId())
                    .customerName(req.getCustomer().getUser().getFullName())
                    .proxySellerId(req.getProxySeller().getId())
                    .proxySellerName(req.getProxySeller().getUser().getFullName())
                    .productDescription(req.getProductDescription())
                    .expectPrice(req.getExpectPrice())
                    .status(req.getStatus().name())
                    .rejectionReason(req.getRejectionReason())
                    .bankAccountNumber(req.getBankAccountNumber())
                    .bankName(req.getBankName())
                    .accountHolderName(req.getAccountHolderName())
                    .imageUrls(imageUrls)
                    .createdAt(req.getCreatedAt())
                    .updatedAt(req.getUpdatedAt())
                    .build();
        });
    }
    @Transactional(readOnly = true)
    @Override
    public DelegationResponse getDelegationRequestDetail(Long id, Long accountId, String role) {
        DelegationRequests request = delegationRequestsRepository.findByIdWithImages(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác"));

        if ("CUSTOMER".equalsIgnoreCase(role) && !request.getCustomer().getId().equals(accountId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        if ("PROXY_SELLER".equalsIgnoreCase(role) && !request.getProxySeller().getId().equals(accountId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        Set<String> imageUrls = request.getImages() != null
                ? request.getImages().stream().map(DelegationImages::getImageUrl).collect(Collectors.toSet())
                : Collections.emptySet();

        return DelegationResponse.builder()
                .id(request.getId())
                .customerId(request.getCustomer().getId())
                .customerName(request.getCustomer().getUser().getFullName())
                .proxySellerId(request.getProxySeller().getId())
                .proxySellerName(request.getProxySeller().getUser().getFullName())
                .productDescription(request.getProductDescription())
                .expectPrice(request.getExpectPrice())
                .status(request.getStatus().name())
                .rejectionReason(request.getRejectionReason())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .imageUrls(imageUrls)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
    @Override
    public void markAsInTransit(Long delegationId, Long customerId) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác."));

        if (!request.getCustomer().getId().equals(customerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền thực hiện hành động này.");
        }

        if (request.getStatus() != DelegationRequestsStatus.APPROVED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể giao sản phẩm từ trạng thái 'Đã duyệt' (APPROVED).");
        }

        request.setStatus(DelegationRequestsStatus.IN_TRANSIT);
        delegationRequestsRepository.save(request);

        String inTransitMessage = String.format("Khách hàng %s đã bắt đầu giao sản phẩm \"%s\" cho bạn.",
                request.getCustomer().getUser().getFullName(),
                request.getProductDescription());

        notificationService.createNotification(
                request.getProxySeller().getId(),
                "Sản phẩm ủy thác đang được giao",
                inTransitMessage,
                13,
                3,
                "DelegationRequest",
                delegationId
        );
    }
    @Override
    public void markAsProductReceived(Long delegationId, Long proxySellerId) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác."));

        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền cập nhật yêu cầu này");
        }

        if (request.getStatus() != DelegationRequestsStatus.IN_TRANSIT) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể xác nhận nhận hàng từ trạng thái 'Đang vận chuyển'.");
        }

        request.setStatus(DelegationRequestsStatus.PRODUCT_RECEIVED);
        request.setProductDeliveryDate(LocalDateTime.now());
        delegationRequestsRepository.save(request);

        String message = String.format("Proxy seller đã xác nhận nhận được sản phẩm \"%s\" của bạn.",
                request.getProductDescription());

        notificationService.createNotification(
                request.getCustomer().getId(),
                "Đã nhận hàng ký gửi",
                message,
                14,
                3,
                "DelegationRequest",
                delegationId
        );
    }

    @Override
    public void markAsSelling(Long delegationId, Long proxySellerId) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác."));

        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền cập nhật yêu cầu này");
        }

        if (request.getStatus() != DelegationRequestsStatus.PRODUCT_RECEIVED) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể đăng bán sản phẩm từ trạng thái 'Đã nhận hàng'.");
        }

        request.setStatus(DelegationRequestsStatus.SELLING);
        delegationRequestsRepository.save(request);

        String message = String.format("Sản phẩm ký gửi \"%s\" của bạn đã được đăng bán.",
                request.getProductDescription());

        notificationService.createNotification(
                request.getCustomer().getId(),
                "Sản phẩm đã được đăng bán",
                message,
                15,
                3,
                "DelegationRequest",
                delegationId
        );
    }

    @Override
    public void markAsSold(Long delegationId, Long proxySellerId, BigDecimal soldPrice) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác."));

        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền cập nhật yêu cầu này");
        }

        if (request.getStatus() != DelegationRequestsStatus.SELLING) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể xác nhận 'Đã bán' từ trạng thái 'Đang bán'.");
        }

        request.setStatus(DelegationRequestsStatus.SOLD);
        request.setSoldPrice(soldPrice);
        request.setSoldDate(LocalDateTime.now());

        DelegationRequests savedRequest = delegationRequestsRepository.saveAndFlush(request);

        BigDecimal finalSoldPrice = savedRequest.getSoldPrice();
        BigDecimal proxyCommission = savedRequest.getCommissionFee();
        Account proxySeller = savedRequest.getProxySeller();
        int month = savedRequest.getSoldDate().getMonthValue();
        int year = savedRequest.getSoldDate().getYear();

        ProxySellerMonthlyRevenue revenueRecord = revenueRepository
                .findByProxySellerIdAndMonthAndYear(proxySeller.getId(), month, year)
                .orElseGet(() -> {
                    return ProxySellerMonthlyRevenue.builder()
                            .proxySeller(proxySeller)
                            .month(month)
                            .year(year)
                            .totalConsignments(0)
                            .completedConsignments(0)
                            .totalSalesAmount(BigDecimal.ZERO)
                            .totalCommission(BigDecimal.ZERO)
                            .adminCommissionRate(new BigDecimal("5.00"))
                            .paymentStatus(PaymentStatus.NOT_DUE)
                            .build();
                });
        BigDecimal finalPriceToAdd = (finalSoldPrice != null) ? finalSoldPrice : BigDecimal.ZERO;
        BigDecimal commissionToAdd = (proxyCommission != null) ? proxyCommission : BigDecimal.ZERO;

        revenueRecord.setCompletedConsignments(revenueRecord.getCompletedConsignments() + 1);
        revenueRecord.setTotalSalesAmount(revenueRecord.getTotalSalesAmount().add(finalPriceToAdd));
        revenueRecord.setTotalCommission(revenueRecord.getTotalCommission().add(commissionToAdd));

        revenueRepository.save(revenueRecord);


        String message = String.format("Sản phẩm ký gửi \"%s\" của bạn đã được bán thành công.",
                request.getProductDescription(),
                soldPrice.toString()
        );
        notificationService.createNotification(
                request.getCustomer().getId(),
                "Sản phẩm đã bán",
                message,
                16,
                3,
                "DelegationRequest",
                delegationId
        );
    }

    @Override
    public void markAsPaymentCompleted(Long delegationId, Long proxySellerId) {
        DelegationRequests request = delegationRequestsRepository.findById(delegationId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REQUEST, "Không tìm thấy yêu cầu ủy thác."));

        if (!request.getProxySeller().getId().equals(proxySellerId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền cập nhật yêu cầu này");
        }

        if (request.getStatus() != DelegationRequestsStatus.SOLD) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Chỉ có thể hoàn tất thanh toán từ trạng thái 'Đã bán'.");
        }

        request.setStatus(DelegationRequestsStatus.PAYMENT_COMPLETED);
        request.setPaymentToCustomerDate(LocalDateTime.now());
        delegationRequestsRepository.save(request);

        String message = String.format("Thanh toán cho sản phẩm ký gửi \"%s\" đã được hoàn tất. Cảm ơn bạn!",
                request.getProductDescription());

        notificationService.createNotification(
                request.getCustomer().getId(),
                "Hoàn tất thanh toán ký gửi",
                message,
                17,
                3,
                "DelegationRequest",
                delegationId
        );
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ProxySellerInfoResponse> getAvailableProxySellers(Pageable pageable) {
        return accountRepository.findAvailableProxySellers(pageable);
    }
}
