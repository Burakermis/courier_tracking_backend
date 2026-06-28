package com.migros.couriertracking.service;

import org.springframework.stereotype.Service;

/**
 * Service for calculating geographical distances using the Haversine formula.
 * The Haversine formula determines the great-circle distance between two points
 * on a sphere given their latitudes and longitudes.
 */
@Service
public class HaversineService {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    /**
     * Calculates the distance between two geographical coordinates in meters.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @return Distance in meters
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}
