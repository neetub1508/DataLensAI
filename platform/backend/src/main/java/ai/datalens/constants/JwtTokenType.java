package ai.datalens.constants;

public final class JwtTokenType {
    public static final String ACCESS = "ACCESS";
    public static final String REFRESH = "REFRESH";
    public static final String VERIFICATION = "VERIFICATION";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";
    
    private JwtTokenType() {
        // Prevent instantiation
    }
}