package com.org.share_recycled_stuff.service.impl;

import com.org.share_recycled_stuff.dto.response.GeoLocation;
import com.org.share_recycled_stuff.service.GeoIpService;
import com.org.share_recycled_stuff.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoIpServiceImpl implements GeoIpService {

    private static final String GEO_IP_API_URL = "http://ip-api.com/json/";
    private final RestTemplate restTemplate;

    @Override
    public GeoLocation getLocationFromIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            log.warn("IP address is null or empty");
            return null;
        }

        // Check if IP is local/private
        if (IpUtils.isLocalIp(ipAddress)) {
            log.debug("IP {} is a local/private address, skipping GeoIP lookup", ipAddress);
            return createDefaultLocation();
        }

        try {
            String url = GEO_IP_API_URL + ipAddress;
            log.debug("Fetching location for IP: {} from {}", ipAddress, url);

            GeoLocation location = restTemplate.getForObject(url, GeoLocation.class);

            if (location != null && "success".equalsIgnoreCase(location.getStatus())) {
                log.info("Successfully retrieved location for IP {}: {}, {}",
                        ipAddress, location.getCity(), location.getCountry());
                return location;
            } else {
                log.warn("Failed to retrieve location for IP {}: {}",
                        ipAddress, location != null ? location.getStatus() : "null response");
                return null;
            }

        } catch (Exception e) {
            log.error("Error fetching GeoIP data for IP {}: {}", ipAddress, e.getMessage());
            return null;
        }
    }

    private GeoLocation createDefaultLocation() {
        return GeoLocation.builder()
                .latitude(21.0285)
                .longitude(105.8542)
                .city("Hanoi")
                .country("Vietnam")
                .regionName("Hanoi")
                .status("success")
                .build();
    }
}

