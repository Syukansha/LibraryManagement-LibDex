package com.Syukri.LibraryManagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class LibraryManagementApplication {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LibraryManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementApplication.class, args);
	}

    @Bean
    public CommandLineRunner applicationStartupRunner() {
        return args -> log.info("*** STARTED:  Application successfully started ***");
    }

    @Bean
    public CommandLineRunner checkPostgresConnection(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                log.info("*** LibDex Service Started ***");
                String dbName = connection.getCatalog();
                log.info("✅ Successfully connected to PostgreSQL database: " + dbName);
                System.out.println("🔗 URL: " + connection.getMetaData().getURL());
            } catch (Exception e) {
                System.err.println("❌ Failed to connect to PostgreSQL: " + e.getMessage());
                throw new RuntimeException("PostgreSQL connection failed on startup", e);
            }
        };
    }

}
