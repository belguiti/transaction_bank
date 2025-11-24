package com.bank_fx.transaction.adapters.web;


import com.bank_fx.transaction.application.dto.UserRegistrationDto;
import com.bank_fx.transaction.application.service.UserService;
import com.bank_fx.transaction.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        User user = userService.registerUser(registrationDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<User> blockUser(@PathVariable Long userId) {
        User user = userService.blockUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/unblock")
    public ResponseEntity<User> unblockUser(@PathVariable Long userId) {
        User user = userService.unblockUser(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }
}