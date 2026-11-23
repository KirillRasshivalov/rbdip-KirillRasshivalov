package com.rbdip.bookstore.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
class LoadDuringMigrationTest {

    private static final int READER_THREADS = 4;
    private static final int MIN_SUCCESSFUL_READS = 50;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bookstore_migration_load")
            .withUsername("bookstore")
            .withPassword("bookstore");

    @Test
    void expandAndBackfillDoNotBreakLegacyFullNameReads() throws Exception {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUser(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());

        MigrationInfo[] pending = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .info()
                .pending();

        assertThat(pending.length)
                .as("Ожидаются миграции expand-contract сверх V1")
                .isGreaterThan(1);

        Flyway.configure().dataSource(dataSource).target("1").load().migrate();

        String lastBeforeContract = pending[pending.length - 2].getVersion().getVersion();

        List<Exception> errors = new CopyOnWriteArrayList<>();
        AtomicInteger successfulReads = new AtomicInteger();
        AtomicBoolean keepReading = new AtomicBoolean(true);
        CountDownLatch started = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(READER_THREADS);

        Runnable reader = () -> {
            started.countDown();
            while (keepReading.get()) {
                try (Connection connection = dataSource.getConnection();
                        Statement statement = connection.createStatement()) {
                    statement.executeQuery("SELECT customer_full_name FROM orders LIMIT 1");
                    successfulReads.incrementAndGet();
                } catch (SQLException exception) {
                    errors.add(exception);
                }
            }
        };

        for (int i = 0; i < READER_THREADS; i++) {
            pool.submit(reader);
        }
        started.await(5, TimeUnit.SECONDS);

        Flyway.configure().dataSource(dataSource).target(lastBeforeContract).load().migrate();

        keepReading.set(false);
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(errors).isEmpty();
        assertThat(successfulReads.get()).isGreaterThanOrEqualTo(MIN_SUCCESSFUL_READS);
    }
}
