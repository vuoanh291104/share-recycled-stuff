package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.request.UpgradeRequestDTO;
import com.org.share_recycled_stuff.dto.response.UpgradeRequestResponse;

public interface ProxySellerService {
    UpgradeRequestResponse createRequest(UpgradeRequestDTO upgradeRequestDTO, String email);
}
