package ai.datalens.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit"
    )
    private String password;

    @Size(max = 10, message = "Locale must not exceed 10 characters")
    @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$", message = "Locale must be in format 'en' or 'en-US'")
    private String locale = "en";

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String locale) {
        this.email = email;
        this.password = password;
        this.locale = locale;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String toString() {
        return "RegisterRequest{email='" + email + "', locale='" + locale + "'}";
    }
}