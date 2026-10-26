-- Identidad y acceso. Las credenciales viven en Supabase Auth (externo);
-- aqui solo se guarda el perfil de negocio, enlazado por user_id (UUID emitido por Supabase).

CREATE TABLE clients (
    id CHAR(36) PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    document VARCHAR(30) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(160) NOT NULL,
    phone VARCHAR(30),
    city VARCHAR(80),
    notification_channel VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    reminders_enabled TINYINT(1) NOT NULL DEFAULT 1,
    status VARCHAR(25) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT uk_clients_document UNIQUE (document),
    CONSTRAINT uk_clients_email UNIQUE (email),
    CONSTRAINT chk_clients_status CHECK (status IN ('PENDING_VERIFICATION','ACTIVE','SUSPENDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE providers (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    legal_name VARCHAR(200) NOT NULL,
    nit VARCHAR(30) NOT NULL,
    category VARCHAR(60),
    contact_email VARCHAR(160) NOT NULL,
    contact_phone VARCHAR(30),
    timezone VARCHAR(60) NOT NULL,
    status VARCHAR(25) NOT NULL DEFAULT 'PENDING_APPROVAL',
    approved_at TIMESTAMP(6) NULL,
    min_booking_notice_minutes INT NOT NULL DEFAULT 0,
    max_booking_notice_minutes INT NOT NULL DEFAULT 43200,
    free_cancellation_window_minutes INT NOT NULL DEFAULT 1440,
    CONSTRAINT uk_providers_nit UNIQUE (nit),
    CONSTRAINT chk_providers_status CHECK (status IN ('PENDING_APPROVAL','ACTIVE','SUSPENDED')),
    CONSTRAINT chk_providers_notice CHECK (min_booking_notice_minutes <= max_booking_notice_minutes)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- La collation *_ai_ci (accent/case insensitive) en esta tabla es lo que
-- hace que la busqueda del catalogo (HU-005) ignore tildes y mayusculas
-- sin necesidad de una funcion UNACCENT (que MySQL no tiene nativamente).

CREATE TABLE provider_owners (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    provider_id CHAR(36) NOT NULL,
    CONSTRAINT uk_provider_owners_user UNIQUE (user_id),
    CONSTRAINT uk_provider_owners_provider UNIQUE (provider_id),
    CONSTRAINT fk_provider_owners_provider FOREIGN KEY (provider_id) REFERENCES providers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE provider_staff (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    provider_id CHAR(36) NOT NULL,
    role VARCHAR(30) NOT NULL,
    CONSTRAINT uk_provider_staff_user_provider UNIQUE (user_id, provider_id),
    CONSTRAINT fk_provider_staff_provider FOREIGN KEY (provider_id) REFERENCES providers(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE admin_users (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    mfa_enabled TINYINT(1) NOT NULL DEFAULT 0,
    CONSTRAINT uk_admin_users_user UNIQUE (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE locations (
    id CHAR(36) PRIMARY KEY,
    provider_id CHAR(36) NOT NULL,
    name VARCHAR(120) NOT NULL,
    address VARCHAR(200),
    city VARCHAR(80),
    timezone VARCHAR(60) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_locations_provider FOREIGN KEY (provider_id) REFERENCES providers(id),
    CONSTRAINT chk_locations_status CHECK (status IN ('ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE role (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    CONSTRAINT uk_role_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE permission (
    id CHAR(36) PRIMARY KEY,
    code VARCHAR(60) NOT NULL,
    description VARCHAR(200),
    CONSTRAINT uk_permission_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE role_permission (
    role_id CHAR(36) NOT NULL,
    permission_id CHAR(36) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_role (
    user_id CHAR(36) PRIMARY KEY,
    role_id CHAR(36) NOT NULL,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE audit_log (
    id CHAR(36) PRIMARY KEY,
    actor_id CHAR(36) NOT NULL,
    role VARCHAR(40) NOT NULL,
    action VARCHAR(60) NOT NULL,
    entity_type VARCHAR(60) NOT NULL,
    entity_id CHAR(36) NOT NULL,
    result VARCHAR(20) NOT NULL,
    provider_id CHAR(36) NULL,
    occurred_at TIMESTAMP(6) NOT NULL,
    INDEX idx_audit_log_provider (provider_id),
    INDEX idx_audit_log_occurred_at (occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO role (id, name) VALUES
    (UUID(), 'ADMINISTRADOR'),
    (UUID(), 'GERENTE'),
    (UUID(), 'PROVEEDOR'),
    (UUID(), 'RECEPCIONISTA'),
    (UUID(), 'CLIENTE');
