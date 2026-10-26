package com.codefactory.bookingplatform.booking.infrastructure;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba de integracion que demuestra, sin depender de Spring, el mecanismo
 * central de NN-03 en MySQL: la restriccion UNIQUE(resource_id, slot_start)
 * sobre resource_slot_lock. Dos hilos intentan insertar el MISMO slot para
 * el MISMO recurso al mismo tiempo; exactamente uno debe tener exito.
 *
 * Requiere Docker disponible en el entorno donde corre (CI de GitHub
 * Actions lo trae por defecto); no se ejecuta en el sandbox de generacion
 * de este proyecto por no tener acceso a Docker-in-Docker ni a Maven Central.
 */
@Testcontainers
class ResourceSlotLockConcurrencyIT {

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("bookingplatform_test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void startContainerAndCreateTable() throws SQLException {
        mysql.start();
        try (Connection conn = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
             Statement statement = conn.createStatement()) {
            statement.execute("""
                    CREATE TABLE resource_slot_lock (
                        id CHAR(36) PRIMARY KEY,
                        resource_id CHAR(36) NOT NULL,
                        booking_id CHAR(36) NOT NULL,
                        slot_start TIMESTAMP(6) NOT NULL,
                        CONSTRAINT uk_resource_slot_lock UNIQUE (resource_id, slot_start)
                    ) ENGINE=InnoDB
                    """);
        }
    }

    @AfterAll
    static void stopContainer() {
        mysql.stop();
    }

    @Test
    void only_one_concurrent_insert_succeeds_for_the_same_resource_and_slot() throws Exception {
        String resourceId = java.util.UUID.randomUUID().toString();
        String slotStart = "2026-11-01 10:00:00.000000";

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch goLatch = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger failures = new AtomicInteger();

        Callable<Void> attempt = (() -> {
            readyLatch.countDown();
            goLatch.await();
            try (Connection conn = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
                 Statement statement = conn.createStatement()) {
                statement.executeUpdate(String.format(
                        "INSERT INTO resource_slot_lock (id, resource_id, booking_id, slot_start) VALUES ('%s', '%s', '%s', '%s')",
                        java.util.UUID.randomUUID(), resourceId, java.util.UUID.randomUUID(), slotStart));
                successes.incrementAndGet();
            } catch (SQLException ex) {
                failures.incrementAndGet();
            }
            return null;
        });

        Future<Void> f1 = executor.submit(attempt);
        Future<Void> f2 = executor.submit(attempt);
        readyLatch.await();
        goLatch.countDown();
        f1.get(10, TimeUnit.SECONDS);
        f2.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successes.get()).isEqualTo(1);
        assertThat(failures.get()).isEqualTo(1);
    }
}
