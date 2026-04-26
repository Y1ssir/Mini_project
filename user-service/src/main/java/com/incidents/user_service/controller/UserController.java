package com.incidents.user_service.controller;

import com.incidents.user_service.dto.UserResponseDTO;
import com.incidents.user_service.entity.User;
import com.incidents.user_service.service.MinioService;
import com.incidents.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final MinioService minioService;

    public UserController(UserService userService, MinioService minioService) {
        this.userService = userService;
        this.minioService = minioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaim("email");
        String firstName = jwt.getClaim("given_name");
        String lastName = jwt.getClaim("family_name");

        var userOpt = userService.getUserByKeycloakId(keycloakId);
        User user;

        if (userOpt.isEmpty()) {
            user = userService.createUser(keycloakId, firstName, lastName, email);
        } else {
            user = userOpt.get();
        }

        return ResponseEntity.ok(convertToDTO(user));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> updates) {

        String keycloakId = jwt.getSubject();
        var userOpt = userService.getUserByKeycloakId(keycloakId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        if (updates.containsKey("department")) {
            user.setDepartment(updates.get("department"));
        }
        if (updates.containsKey("position")) {
            user.setPosition(updates.get("position"));
        }

        return ResponseEntity.ok(convertToDTO(userService.updateUser(user)));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file) {

        try {
            String avatarUrl = minioService.uploadAvatar(file);

            String keycloakId = jwt.getSubject();
            var userOpt = userService.getUserByKeycloakId(keycloakId);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setAvatarUrl(avatarUrl);
                userService.updateUser(user);
            }

            Map<String, String> response = new HashMap<>();
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getDepartment(),
                user.getPosition()
        );
    }
}