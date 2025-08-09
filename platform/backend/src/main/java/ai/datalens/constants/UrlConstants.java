package ai.datalens.constants;

public final class UrlConstants {
    
    // Development URLs
    public static final String FRONTEND_DEV_URL = "http://localhost:3000";
    public static final String BACKEND_DEV_URL = "http://localhost:8000";
    public static final String DATABASE_DEV_URL = "jdbc:postgresql://localhost:5433/datalensai";
    public static final String REDIS_DEV_HOST = "localhost";
    public static final int REDIS_DEV_PORT = 6379;
    
    // CORS Origins
    public static final String[] DEV_CORS_ORIGINS = {
        "http://localhost:3000",
        "http://localhost:8080",
        "http://frontend:3000"
    };
    
    // OAuth Callback URLs (Development)
    public static final String GOOGLE_OAUTH_CALLBACK = "/login/oauth2/code/google";
    public static final String FACEBOOK_OAUTH_CALLBACK = "/login/oauth2/code/facebook";
    public static final String GITHUB_OAUTH_CALLBACK = "/login/oauth2/code/github";
    
    // Frontend Auth URLs
    public static final String AUTH_CALLBACK_URL = "http://localhost:3000/auth/callback";
    public static final String EMAIL_VERIFICATION_URL = "http://localhost:3000/verify-email";
    public static final String PASSWORD_RESET_URL = "http://localhost:3000/reset-password";
    
    // Health Check Endpoints
    public static final String HEALTH_CHECK_ENDPOINT = "/actuator/health";
    public static final String SWAGGER_UI_ENDPOINT = "/swagger-ui.html";
    
    // Domain Constants
    public static final String COMPANY_DOMAIN = "datalens.ai";
    public static final String ADMIN_EMAIL = "admin@datalens.ai";
    public static final String USER_EMAIL = "user@datalens.ai";
    public static final String SUPPORT_EMAIL = "support@datalens.ai";
    
    private UrlConstants() {
        // Prevent instantiation
    }
}