package com.migros.couriertracking.service.strategy;

public interface DistanceStrategy {

    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
}
