package com.migros.couriertracking.event;

import com.migros.couriertracking.dto.CourierLocationRequest;
import com.migros.couriertracking.entity.Courier;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class CourierLocationUpdatedEvent extends ApplicationEvent {

    private final Courier courier;
    private final CourierLocationRequest request;
    private final LocalDateTime eventTime;

    public CourierLocationUpdatedEvent(Object source, Courier courier, CourierLocationRequest request, LocalDateTime eventTime) {
        super(source);
        this.courier = courier;
        this.request = request;
        this.eventTime = eventTime;
    }
}
