package ai.datalens.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendVerificationEmail(String email, String verificationToken) {
        // For now, just log the verification email details
        // In production, you would integrate with an email service like SendGrid, SES, etc.
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
        
        logger.info("Sending verification email to: {}", email);
        logger.info("Verification URL: {}", verificationUrl);
        
        // TODO: Implement actual email sending logic
        System.out.println("=".repeat(80));
        System.out.println("EMAIL VERIFICATION");
        System.out.println("To: " + email);
        System.out.println("Subject: Verify your Data Lens AI account");
        System.out.println("Verification Link: " + verificationUrl);
        System.out.println("=".repeat(80));
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        // For now, just log the password reset email details
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
        
        logger.info("Sending password reset email to: {}", email);
        logger.info("Password reset URL: {}", resetUrl);
        
        // TODO: Implement actual email sending logic
        System.out.println("=".repeat(80));
        System.out.println("PASSWORD RESET");
        System.out.println("To: " + email);
        System.out.println("Subject: Reset your Data Lens AI password");
        System.out.println("Reset Link: " + resetUrl);
        System.out.println("=".repeat(80));
    }
}