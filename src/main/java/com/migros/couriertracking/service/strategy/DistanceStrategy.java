package com.migros.couriertracking.service.strategy;

/**
 * Strategy interface for calculating geographical distances.
 */
public interface DistanceStrategy {

    /**
     * Calculates the distance between two geographical coordinates.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @return Distance in meters
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}
