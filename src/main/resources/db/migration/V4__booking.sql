CREATE TABLE booking (
    id CHAR(36) PRIMARY KEY,
    client_id CHAR(36) NOT NULL,
    service_id CHAR(36) NOT NULL,
    location_id CHAR(36) NOT NULL,
    code VARCHAR(20) NOT NULL,
    starts_at TIMESTAMP(6) NOT NULL,
    ends_at TIMESTAMP(6) NOT NULL,
    attendees INT NOT NULL DEFAULT 1,
    total_price DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reschedule_count INT NOT NULL DEFAULT 0,
    manual_confirmation_required TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_booking_code UNIQUE (code),
    CONSTRAINT fk_booking_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_booking_service FOREIGN KEY (service_id) REFERENCES catalog_service(id),
    CONSTRAINT fk_booking_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT chk_booking_order CHECK (ends_at > starts_at),
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','CANCELLED_LATE','COMPLETED','NO_SHOW')),
    INDEX idx_booking_client_time (client_id, starts_at),
    INDEX idx_booking_service_time (service_id, starts_at),
    INDEX idx_booking_location_time (location_id, starts_at),
    INDEX idx_booking_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE booking_condition_snapshot (
    booking_id CHAR(36) PRIMARY KEY,
    min_lead_time_minutes INT NOT NULL,
    max_lead_time_minutes INT NOT NULL,
    free_cancellation_window_minutes INT NOT NULL,
    duration_minutes INT NOT NULL,
    max_reschedules INT NOT NULL DEFAULT 3,
    CONSTRAINT fk_booking_condition_snapshot_booking FOREIGN KEY (booking_id) REFERENCES booking(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE booking_status_change (
    id CHAR(36) PRIMARY KEY,
    booking_id CHAR(36) NOT NULL,
    from_status VARCHAR(20) NOT NULL,
    to_status VARCHAR(20) NOT NULL,
    reason VARCHAR(200),
    changed_by CHAR(36) NOT NULL,
    changed_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_booking_status_change_booking FOREIGN KEY (booking_id) REFERENCES booking(id),
    INDEX idx_booking_status_change_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
