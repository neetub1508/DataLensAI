package ai.datalens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("test-auth")
public class TestSecurityConfig {
    
    @Bean
    public PasswordEncoder testPasswordEncoder() {
        // WARNING: Only for testing! Never use in production!
        return NoOpPasswordEncoder.getInstance();
    }
}