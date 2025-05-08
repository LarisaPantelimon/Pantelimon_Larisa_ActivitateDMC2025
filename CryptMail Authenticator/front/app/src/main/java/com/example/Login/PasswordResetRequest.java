package com.example.Login;

import com.google.gson.annotations.SerializedName;

public class PasswordResetRequest {
    @SerializedName("reset_token")
    private String resetToken;

    @SerializedName("new_password")
    private String newPassword;

    @SerializedName("confirm_password")
    private String confirmPassword;

    public PasswordResetRequest(String resetToken, String newPassword, String confirmPassword) {
        this.resetToken = resetToken;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters
    public String getResetToken() {
        return resetToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    // Setters (if needed)
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Validation method
    public boolean isValid() {
        return resetToken != null && !resetToken.isEmpty() &&
                newPassword != null && newPassword.length() >= 8 &&
                newPassword.equals(confirmPassword);
    }
}
