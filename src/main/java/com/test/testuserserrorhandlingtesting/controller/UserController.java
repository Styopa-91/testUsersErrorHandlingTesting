package com.abi.agro_back.controller;

import com.abi.agro_back.auth.PasswordDto;
import com.abi.agro_back.collection.User;
import com.abi.agro_back.config.MailSender;
import com.abi.agro_back.exception.ResourceNotFoundException;
import com.abi.agro_back.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "the User Endpoint")
public class UserController {

    @Autowired
    private MailSender mailSender;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Validated @RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String  userId,
                                              @RequestBody User updatedUser) {
        User user = userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") String  id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String userEmail) {
        User user = userService.findUserByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException(user.getEmail() + " not found");
        }
        String token = java.util.UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        mailSender.sendResetEmail(token, user.getEmail());

        return ResponseEntity.ok("Link for reset password sent to email");
    }

    @GetMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestParam("token") String token,
                                               @RequestParam("password") String password) {
        String result = userService.validatePasswordResetToken(token);

        if(result != null) {
            return ResponseEntity.ok("Password saved");
        }

        User user = userService.getUserByPasswordResetToken(token);
        if(user != null) {
            userService.changeUserPassword(user, password);
            return ResponseEntity.ok("Password Updated");
        } else {
            return ResponseEntity.ok("Password have not updated");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
