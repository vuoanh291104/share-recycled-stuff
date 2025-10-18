package com.org.share_recycled_stuff.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocationUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate differences
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate distance
        double distance = EARTH_RADIUS_KM * c;

        log.debug("Distance between ({}, {}) and ({}, {}): {} km",
                lat1, lon1, lat2, lon2, distance);

        return distance;
    }

    public static boolean isValidCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }
        return latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }

    public static boolean areValidLocations(Double lat1, Double lon1, Double lat2, Double lon2) {
        return isValidCoordinates(lat1, lon1) && isValidCoordinates(lat2, lon2);
    }
}

