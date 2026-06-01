package project.warehouse.control.dto;

public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String fullName;
    private String role;
    private Long expiresIn;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String email, String fullName, String role, Long expiresIn) {
        this.accessToken = accessToken;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}