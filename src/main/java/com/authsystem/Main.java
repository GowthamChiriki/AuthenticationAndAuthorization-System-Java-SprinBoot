package com.authsystem;

import com.authsystem.controller.AuthController;
import com.authsystem.util.HibernateUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    // Simple DB init using JDBC: create DB (if not exists), create tables, seed roles
    private static void initDatabase() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String pass = "Gowtham@2929"; // change accordingly
        String dbName = "authsystem";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, pass);
             Statement stmt = conn.createStatement()) {
            // create DB if not exists
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("Database ensured: " + dbName);
        } catch (Exception e) {
            System.err.println("Failed to create DB: " + e.getMessage());
            // Continue — Hibernate may fail later if DB not present
        }
    }

    public static void main(String[] args) {
        // 1) ensure DB exists (simple)
        initDatabase();

        // 2) start Hibernate (will create/update tables because hbm2ddl.auto=update)
        HibernateUtil.getSessionFactory();

        System.out.println("Starting CLI. Tip: First create an ADMIN via DB or modify seed role assignment to create admin user.");
        try (Scanner sc = new Scanner(System.in)) {
            AuthController controller = new AuthController(sc);
            controller.runCLI();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
