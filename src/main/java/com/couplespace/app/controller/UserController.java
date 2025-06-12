package com.couplespace.app.controller;

import com.couplespace.app.entity.User;
import com.couplespace.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String gender = request.get("gender");

            User user = userService.registerUser(username, password, gender);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "注册成功",
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "gender", user.getGender()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");

            User user = userService.loginUser(username, password);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "登录成功",
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "gender", user.getGender()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getUserCount() {
        long count = userService.getUserCount();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count
        ));
    }
}