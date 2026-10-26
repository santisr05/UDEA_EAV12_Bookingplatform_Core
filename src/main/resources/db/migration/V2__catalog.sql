CREATE TABLE currency (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    code VARCHAR(3) NOT NULL,
    CONSTRAINT uk_currency_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO currency (id, name, code) VALUES (UUID(), 'Peso colombiano', 'COP');

CREATE TABLE resource_type (
    id CHAR(36) PRIMARY KEY,
    provider_id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    CONSTRAINT uk_resource_type_provider_name UNIQUE (provider_id, name),
    CONSTRAINT fk_resource_type_provider FOREIGN KEY (provider_id) REFERENCES providers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- "catalog_service" (no "service", para evitar la palabra reservada y para
-- distinguirla de @Service de Spring en el codigo). Collation *_ai_ci para
-- que la busqueda de HU-005 ignore tildes y mayusculas.
CREATE TABLE catalog_service (
    id CHAR(36) PRIMARY KEY,
    provider_id CHAR(36) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    duration_minutes INT NOT NULL,
    preparation_minutes INT NOT NULL DEFAULT 0,
    cleanup_minutes INT NOT NULL DEFAULT 0,
    price DECIMAL(12,2) NOT NULL,
    currency_id CHAR(36) NOT NULL,
    capacity_type VARCHAR(20) NOT NULL DEFAULT 'INDIVIDUAL',
    max_capacity INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT uk_catalog_service_provider_name UNIQUE (provider_id, name),
    CONSTRAINT fk_catalog_service_provider FOREIGN KEY (provider_id) REFERENCES providers(id),
    CONSTRAINT fk_catalog_service_currency FOREIGN KEY (currency_id) REFERENCES currency(id),
    CONSTRAINT chk_catalog_service_duration CHECK (duration_minutes > 0),
    CONSTRAINT chk_catalog_service_price CHECK (price >= 0),
    CONSTRAINT chk_catalog_service_capacity CHECK (max_capacity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE service_location (
    service_id CHAR(36) NOT NULL,
    location_id CHAR(36) NOT NULL,
    PRIMARY KEY (service_id, location_id),
    CONSTRAINT fk_service_location_service FOREIGN KEY (service_id) REFERENCES catalog_service(id),
    CONSTRAINT fk_service_location_location FOREIGN KEY (location_id) REFERENCES locations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE service_resource_requirement (
    id CHAR(36) PRIMARY KEY,
    service_id CHAR(36) NOT NULL,
    resource_type_id CHAR(36) NOT NULL,
    quantity_required INT NOT NULL,
    CONSTRAINT uk_service_resource_requirement UNIQUE (service_id, resource_type_id),
    CONSTRAINT fk_srr_service FOREIGN KEY (service_id) REFERENCES catalog_service(id),
    CONSTRAINT fk_srr_resource_type FOREIGN KEY (resource_type_id) REFERENCES resource_type(id),
    CONSTRAINT chk_srr_quantity CHECK (quantity_required > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
