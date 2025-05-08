package com.example.API;

import com.example.Login.BasicResponse;
import com.example.Login.EmailVerificationRequest;
import com.example.Login.ForgotPasswordRequest;
import com.example.Login.ForgotPasswordResponse;
import com.example.Login.InfoUser;
import com.example.Login.LoginRequest;
import com.example.Login.LoginResponse;
import com.example.Login.PasswordResetRequest;
import com.example.API.RegisterRequest;
import com.example.API.RegisterResponse;
import com.example.Login.ResetCodeRequest;
import com.example.Login.ResetPasswordRequest;
import com.example.Login.SaveEmail;
import com.example.Login.SendCredentials;
import com.example.Login.SendZPK;
import com.example.Login.TokenRefreshRequest;
import com.example.Login.TokenRefreshResponse;
import com.example.Login.TrueBasicResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIInterface {
    // Authentication Endpoints
    @POST("/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    // Password Reset Flow - Two Separate Endpoints
    @POST("/forgot-password")  // Initiation - sends reset email/link
    Call<ForgotPasswordResponse> sendPasswordResetRequest(@Body ForgotPasswordRequest request);

    @POST("/reset-password")   // Completion - actually changes password
    Call<BasicResponse> confirmPasswordReset(@Body ResetPasswordRequest request);

    // Email Verification
    @POST("/verify-email")
    Call<BasicResponse> verifyEmail(@Body EmailVerificationRequest request);

    @POST("/resend-verification")
    Call<BasicResponse> resendVerificationCode(@Body String email);

    @POST("/verify-reset-code")
    Call<TrueBasicResponse> verifyResetCode(@Body ResetCodeRequest request);

    // Token Management
    @POST("/refresh-token")
    Call<TokenRefreshResponse> refreshToken(@Body TokenRefreshRequest request);

    // Protected Endpoints (example)
//    @POST("user/profile")
//    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String authToken);
    @POST("/save-email-account")
    Call<TrueBasicResponse> saveAnotherAccount(@Body SaveEmail request);

    @POST("/get-info-account")
    Call<InfoUser> getInfoUser(@Body String email);

    @POST("/update-info-user")
    Call<TrueBasicResponse> updateUser(@Body InfoUser user);

    @POST("/send-to-web")
    Call<TrueBasicResponse> sendCredentials(@Body SendCredentials credentials);

    @POST("/send-zpk")
    Call<TrueBasicResponse> sendZPK(@Body SendZPK info);

    @POST("/delete-account")
    Call<TrueBasicResponse>deleteAccount(@Body String email);
}