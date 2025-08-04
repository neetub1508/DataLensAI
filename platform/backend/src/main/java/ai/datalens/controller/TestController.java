package ai.datalens.controller;

import ai.datalens.entity.User;
import ai.datalens.repository.UserRepository;
import ai.datalens.security.UserPrincipal;
import ai.datalens.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/{email}")
    public ResponseEntity<?> testUserLookup(@PathVariable String email) {
        try {
            Optional<User> user = userRepository.findByEmailWithRolesAndPermissions(email);
            Map<String, Object> response = new HashMap<>();
            
            if (user.isPresent()) {
                User u = user.get();
                response.put("found", true);
                response.put("email", u.getEmail());
                response.put("verified", u.getIsVerified());
                response.put("status", u.getStatus());
                response.put("hasPassword", u.getPasswordHash() != null);
                response.put("passwordLength", u.getPasswordHash() != null ? u.getPasswordHash().length() : 0);
                response.put("rolesCount", u.getRoles().size());
            } else {
                response.put("found", false);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/password")
    public ResponseEntity<?> testPasswordEncoding(@RequestBody Map<String, String> request) {
        try {
            String plainPassword = request.get("password");
            String encodedPassword = passwordEncoder.encode(plainPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("plainPassword", plainPassword);
            response.put("encodedPassword", encodedPassword);
            response.put("encodedLength", encodedPassword.length());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> testPasswordVerification(@RequestBody Map<String, String> request) {
        try {
            String plainPassword = request.get("password");
            String email = request.get("email");
            
            Optional<User> user = userRepository.findByEmailWithRolesAndPermissions(email);
            Map<String, Object> response = new HashMap<>();
            
            if (user.isPresent()) {
                String storedHash = user.get().getPasswordHash();
                boolean matches = passwordEncoder.matches(plainPassword, storedHash);
                
                response.put("userFound", true);
                response.put("passwordMatches", matches);
                response.put("storedHashLength", storedHash.length());
                response.put("plainPasswordLength", plainPassword.length());
            } else {
                response.put("userFound", false);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/userdetails/{email}")
    public ResponseEntity<?> testUserDetailsService(@PathVariable String email) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Map<String, Object> response = new HashMap<>();
            
            response.put("username", userDetails.getUsername());
            response.put("enabled", userDetails.isEnabled());
            response.put("accountNonExpired", userDetails.isAccountNonExpired());
            response.put("accountNonLocked", userDetails.isAccountNonLocked());
            response.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            response.put("authoritiesCount", userDetails.getAuthorities().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}