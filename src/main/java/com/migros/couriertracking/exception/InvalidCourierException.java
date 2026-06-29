package com.migros.couriertracking.exception;

public class InvalidCourierException extends RuntimeException {
    public InvalidCourierException(String message) {
        super(message);
    }
}
