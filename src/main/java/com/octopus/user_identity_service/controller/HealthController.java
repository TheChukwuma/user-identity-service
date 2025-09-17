package com.octopus.user_identity_service.controller;

import com.octopus.user_identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final DataSource dataSource;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Check database connection
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                health.put("database", isValid ? "UP" : "DOWN");
                health.put("databaseUrl", connection.getMetaData().getURL());
            }
            
            // Check if we can perform a simple query
            long userCount = userRepository.count();
            health.put("userCount", userCount);
            health.put("status", "UP");
            
        } catch (Exception e) {
            log.error("Health check failed", e);
            health.put("database", "DOWN");
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            return ResponseEntity.status(503).body(health);
        }
        
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "user-identity-service");
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try {
            try (Connection connection = dataSource.getConnection()) {
                dbHealth.put("connection", "SUCCESS");
                dbHealth.put("url", connection.getMetaData().getURL());
                dbHealth.put("driver", connection.getMetaData().getDriverName());
                dbHealth.put("version", connection.getMetaData().getDriverVersion());
                dbHealth.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
                dbHealth.put("databaseVersion", connection.getMetaData().getDatabaseProductVersion());
            }
            
            // Test repository access
            long userCount = userRepository.count();
            dbHealth.put("userCount", userCount);
            dbHealth.put("repositoryAccess", "SUCCESS");
            
        } catch (Exception e) {
            log.error("Database health check failed", e);
            dbHealth.put("connection", "FAILED");
            dbHealth.put("error", e.getMessage());
            return ResponseEntity.status(503).body(dbHealth);
        }
        
        return ResponseEntity.ok(dbHealth);
    }
}
