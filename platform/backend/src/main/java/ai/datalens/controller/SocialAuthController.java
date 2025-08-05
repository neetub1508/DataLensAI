package ai.datalens.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth2/authorization")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SocialAuthController {

    @GetMapping("/{provider}")
    public ResponseEntity<?> socialLogin(@PathVariable String provider) {
        // For now, return a message indicating that OAuth2 setup is needed
        String message = String.format(
            "Social login with %s is not yet configured. " +
            "Please set up OAuth2 credentials for %s in the backend configuration. " +
            "See OAUTH2_SETUP.md for detailed instructions.",
            provider.toUpperCase(),
            provider
        );
        
        return ResponseEntity.ok(Map.of(
            "error", "oauth2_not_configured",
            "message", message,
            "provider", provider,
            "redirect_url", "http://localhost:3000/register?error=oauth2-not-configured&provider=" + provider
        ));
    }
}