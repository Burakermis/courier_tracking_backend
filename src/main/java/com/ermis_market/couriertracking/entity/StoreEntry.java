package com.ermis_market.couriertracking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_entries", indexes = {
    @Index(name = "idx_store_entries_courier_id", columnList = "courier_id"),
    @Index(name = "idx_store_entries_store_id", columnList = "store_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courier_id", nullable = false)
    private String courierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;
}
