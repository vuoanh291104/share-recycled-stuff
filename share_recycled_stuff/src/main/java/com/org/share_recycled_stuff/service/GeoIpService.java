package com.org.share_recycled_stuff.service;

import com.org.share_recycled_stuff.dto.response.GeoLocation;

public interface GeoIpService {

    GeoLocation getLocationFromIp(String ipAddress);
}

