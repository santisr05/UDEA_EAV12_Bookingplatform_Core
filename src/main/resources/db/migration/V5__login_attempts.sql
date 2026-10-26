-- Faltaba en V1: la tabla de auditoria de intentos de login que usa la
-- politica de bloqueo de HU-021 (LoginLockPolicy / LoginAttemptJpaEntity).
CREATE TABLE login_attempts (
                                id CHAR(36) PRIMARY KEY,
                                email VARCHAR(160) NOT NULL,
                                successful TINYINT(1) NOT NULL,
                                occurred_at TIMESTAMP(6) NOT NULL,
                                INDEX idx_login_attempts_email_occurred_at (email, occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;