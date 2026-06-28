package com.migros.couriertracking.exception;

public class CourierNotFoundException extends RuntimeException {

    public CourierNotFoundException(String message) {
        super(message);
    }

    public CourierNotFoundException(String courierId, Throwable cause) {
        super("Courier not found with ID: " + courierId, cause);
    }
}
