package com.authsystem.controller;

import com.authsystem.entity.User;
import com.authsystem.service.AuthService;
import com.authsystem.service.UserService;
import com.authsystem.util.BCryptUtil;
import com.authsystem.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;
import java.util.Scanner;

public class AuthController {
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final Scanner scanner;

    public AuthController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void runCLI() {
        printWelcome();
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": signUp(); break;
                    case "2": login(); break;
                    case "3": adminDemo(); break;
                    case "0": System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid option");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void printWelcome() {
        System.out.println("=== Auth System CLI ===");
    }

    private void printMenu() {
        System.out.println("\n1) Signup\n2) Login\n3) Demo Admin actions (requires token)\n0) Exit");
        System.out.print("Choose: ");
    }

    private void signUp() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User u = authService.register(username, email, password);
        System.out.println("Registered: " + u);
    }

    private void login() {
        System.out.print("Username or Email: ");
        String id = scanner.nextLine().trim();
        System.out.print("Password: ");
        String pwd = scanner.nextLine().trim();

        String token = authService.login(id, pwd);
        System.out.println("Login success! JWT token (store it):\n" + token);
        System.out.println("You can use this token in Demo Admin actions.");
    }

    private void adminDemo() {
        System.out.print("Paste your JWT token: ");
        String token = scanner.nextLine().trim();
        try {
            Jws<Claims> claims = JwtUtil.validateToken(token);
            String role = claims.getBody().get("role", String.class);
            Long userId = Long.parseLong(claims.getBody().getSubject());
            String username = claims.getBody().get("username", String.class);

            System.out.println("Authenticated as: " + username + " (" + role + ")");
            if (!"ADMIN".equals(role)) {
                System.out.println("You are not ADMIN. Access to admin operations denied.");
                // But let user view own details:
                User u = userService.getById(userId);
                System.out.println("Your profile: " + u);
                return;
            }

            // Admin menu:
            boolean back = false;
            while (!back) {
                System.out.println("\nADMIN MENU:");
                System.out.println("1) View all users");
                System.out.println("2) Create user");
                System.out.println("3) Delete user");
                System.out.println("4) Assign role to user");
                System.out.println("0) Back");
                System.out.print("Choice: ");
                String ch = scanner.nextLine().trim();
                switch (ch) {
                    case "1":
                        List<User> all = userService.getAllUsers();
                        all.forEach(System.out::println);
                        break;
                    case "2":
                        System.out.print("New username: "); String nu = scanner.nextLine().trim();
                        System.out.print("New email: "); String ne = scanner.nextLine().trim();
                        System.out.print("New password: "); String np = scanner.nextLine().trim();
                        System.out.print("Role (ADMIN/USER): "); String rname = scanner.nextLine().trim();
                        String hash = BCryptUtil.hashPassword(np);
                        User created = userService.createUserAsAdmin(nu, ne, hash, rname);
                        System.out.println("Created: " + created);
                        break;
                    case "3":
                        System.out.print("User id to delete: "); Long delId = Long.parseLong(scanner.nextLine().trim());
                        userService.deleteUser(delId);
                        System.out.println("Deleted user " + delId);
                        break;
                    case "4":
                        System.out.print("User id to assign role: "); Long uid = Long.parseLong(scanner.nextLine().trim());
                        System.out.print("Role name (ADMIN/USER): "); String rr = scanner.nextLine().trim();
                        userService.assignRole(uid, rr);
                        System.out.println("Assigned role.");
                        break;
                    case "0": back = true; break;
                    default: System.out.println("Invalid");
                }
            }

        } catch (Exception ex) {
            System.out.println("Invalid or expired token: " + ex.getMessage());
        }
    }
}
