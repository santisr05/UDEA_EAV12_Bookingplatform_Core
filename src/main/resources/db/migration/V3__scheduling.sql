CREATE TABLE resource (
    id CHAR(36) PRIMARY KEY,
    resource_type_id CHAR(36) NOT NULL,
    location_id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT fk_resource_type FOREIGN KEY (resource_type_id) REFERENCES resource_type(id),
    CONSTRAINT fk_resource_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT chk_resource_capacity CHECK (capacity > 0),
    CONSTRAINT chk_resource_status CHECK (status IN ('AVAILABLE','RETIRED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE schedule (
    id CHAR(36) PRIMARY KEY,
    location_id CHAR(36) NOT NULL,
    resource_id CHAR(36) NULL,
    granularity_minutes INT NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_schedule_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT fk_schedule_resource FOREIGN KEY (resource_id) REFERENCES resource(id),
    CONSTRAINT chk_schedule_granularity CHECK (granularity_minutes IN (15, 30, 60))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE schedule_service (
    schedule_id CHAR(36) NOT NULL,
    service_id CHAR(36) NOT NULL,
    PRIMARY KEY (schedule_id, service_id),
    CONSTRAINT fk_schedule_service_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(id),
    CONSTRAINT fk_schedule_service_service FOREIGN KEY (service_id) REFERENCES catalog_service(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE schedule_block (
    id CHAR(36) PRIMARY KEY,
    schedule_id CHAR(36) NOT NULL,
    day_of_week TINYINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_schedule_block_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(id),
    CONSTRAINT chk_schedule_block_day CHECK (day_of_week BETWEEN 0 AND 6),
    CONSTRAINT chk_schedule_block_order CHECK (end_time > start_time),
    INDEX idx_schedule_block_schedule_day (schedule_id, day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE schedule_exception (
    id CHAR(36) PRIMARY KEY,
    schedule_id CHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL,
    starts_at TIMESTAMP(6) NOT NULL,
    ends_at TIMESTAMP(6) NOT NULL,
    reason VARCHAR(200),
    recurring TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT fk_schedule_exception_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(id),
    CONSTRAINT chk_schedule_exception_type CHECK (type IN ('BLOCK','SPECIAL_HOURS')),
    CONSTRAINT chk_schedule_exception_order CHECK (ends_at > starts_at),
    INDEX idx_schedule_exception_range (schedule_id, starts_at, ends_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- HU-010 / NN-03. Sustituye al EXCLUDE USING gist de PostgreSQL: el tiempo
-- se discretiza segun la granularidad de la agenda (schedule.granularity_minutes)
-- y cada unidad discreta que ocupa una reserva es una fila con una restriccion
-- UNIQUE sobre (resource_id, slot_start). InnoDB rechaza, de forma atomica,
-- un segundo INSERT para el mismo recurso en el mismo instante, incluso bajo
-- transacciones concurrentes -- es la misma garantia que el EXCLUDE de
-- Postgres, solo que expresada sobre instantes discretos en vez de un rango
-- continuo (ver ADR-05 del proyecto).
CREATE TABLE resource_slot_lock (
    id CHAR(36) PRIMARY KEY,
    resource_id CHAR(36) NOT NULL,
    booking_id CHAR(36) NOT NULL,
    slot_start TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_resource_slot_lock UNIQUE (resource_id, slot_start),
    CONSTRAINT fk_resource_slot_lock_resource FOREIGN KEY (resource_id) REFERENCES resource(id),
    INDEX idx_resource_slot_lock_booking (booking_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE resource_maintenance_window (
    id CHAR(36) PRIMARY KEY,
    resource_id CHAR(36) NOT NULL,
    starts_at TIMESTAMP(6) NOT NULL,
    ends_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_resource_maintenance_resource FOREIGN KEY (resource_id) REFERENCES resource(id),
    CONSTRAINT chk_resource_maintenance_order CHECK (ends_at > starts_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
