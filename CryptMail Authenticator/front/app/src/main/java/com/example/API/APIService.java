package com.example.API;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.view.SupportActionModeWrapper;

import com.example.Login.BasicResponse;
import com.example.Login.EmailVerificationRequest;
import com.example.Login.ForgotPasswordRequest;
import com.example.Login.ForgotPasswordResponse;
import com.example.Login.InfoUser;
import com.example.Login.LoginRequest;
import com.example.Login.LoginResponse;
import com.example.Login.PasswordResetRequest;
import com.example.Login.ResetCodeRequest;
import com.example.Login.ResetPasswordRequest;
import com.example.Login.SaveEmail;
import com.example.Login.SendCredentials;
import com.example.Login.SendZPK;
import com.example.Login.TrueBasicResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIService {
    private final Context context;
    private APIInterface api;

    public APIService(Context context) {
        this.context = context;
        api = RetrofitClient.getClient();
    }

    public void verifyEmail(EmailVerificationRequest request,
                            APIResponseCallback<BasicResponse> callback) {
        api.verifyEmail(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = response.errorBody() != null ?
                            response.errorBody().toString() : "Email verification failed";
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // New login method
    public void loginUser(LoginRequest request, final APIResponseCallback<LoginResponse> callback) {
        api.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("Empty response body");
                    }
                } else {
                    // Handle different HTTP error codes
                    String errorMessage = "Login failed";
                    if (response.code() == 401) {
                        errorMessage = "Invalid credentials";
                    } else if (response.code() == 404) {
                        errorMessage = "User not found";
                    }
                    callback.onError(errorMessage + " (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void sendEmail(SaveEmail request, final APIResponseCallback<TrueBasicResponse> callback){
        api.saveAnotherAccount(request).enqueue(new Callback<TrueBasicResponse>() {
            @Override
            public void onResponse(Call<TrueBasicResponse> call, Response<TrueBasicResponse> response) {
                if (response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Empty response body");
                }
            }

            @Override
            public void onFailure(Call<TrueBasicResponse> call, Throwable t) {
                callback.onError("Failed to save the email: " + t.getMessage());
            }
        });
    }

    public void registerUser(RegisterRequest request, final APIResponseCallback<RegisterResponse> callback) {
        api.registerUser(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = response.errorBody() != null ?
                            response.errorBody().toString() : "Registration failed";
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void resendVerificationCode(String email ,APIResponseCallback<BasicResponse> callback) {
        api.resendVerificationCode(email)
                .enqueue(new Callback<BasicResponse>() {
                    @Override
                    public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                        callback.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(Call<BasicResponse> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }
    public void sendPasswordResetRequest(ForgotPasswordRequest request,
                                         APIResponseCallback<ForgotPasswordResponse> callback) {
        api.sendPasswordResetRequest(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call,
                                   Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = response.errorBody() != null ?
                            response.errorBody().toString() : "Password reset request failed";
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    public void resetPassword(ResetPasswordRequest request,
                              APIResponseCallback<BasicResponse> callback) {
        //ResetPasswordRequest request = new ResetPasswordRequest(email, newPassword);

        api.confirmPasswordReset(request).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = response.errorBody() != null ?
                            response.errorBody().toString() : "Password reset failed";
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    public void getInfoUser(String email, APIResponseCallback<InfoUser> callback) {
        api.getInfoUser(email).enqueue(new Callback<InfoUser>() {
            @Override
            public void onResponse(Call<InfoUser> call, Response<InfoUser> response) {
                System.out.println("Raspundul de la SERVER: "+response.body());
                if (response.isSuccessful() && response.body() != null) {
                    // Successful response with data
                    callback.onSuccess(response.body());
                } else {
                    // Server returned error response
                    String errorMessage = "Server error: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<InfoUser> call, Throwable t) {
                // Network failure or other exceptions
                String errorMessage = "Network error: " + t.getMessage();
                callback.onError(errorMessage);
            }
        });
    }

    public void updateInfoUser(InfoUser request, APIResponseCallback<TrueBasicResponse> callback){
        api.updateUser(request).enqueue(new Callback<TrueBasicResponse>() {
            @Override
            public void onResponse(Call<TrueBasicResponse> call, Response<TrueBasicResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<TrueBasicResponse> call, Throwable t) {
                callback.onError("Error updating user: " + t.getMessage());
            }
        });
    }

    public void sendUserCredentials(SendCredentials credentials, APIResponseCallback<TrueBasicResponse>callback){
        api.sendCredentials(credentials).enqueue(new Callback<TrueBasicResponse>() {
            @Override
            public void onResponse(Call<TrueBasicResponse> call, Response<TrueBasicResponse> response) {
                //callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<TrueBasicResponse> call, Throwable t) {
                callback.onError("Error on sending the credentials! "+ t.getMessage());
            }
        });
    }
    public void sendZPK(SendZPK info, APIResponseCallback<TrueBasicResponse> callback){
        api.sendZPK(info).enqueue(new Callback<TrueBasicResponse>() {
            @Override
            public void onResponse(Call<TrueBasicResponse> call, Response<TrueBasicResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<TrueBasicResponse> call, Throwable t) {
                callback.onError("Error on sending the ZPK! "+ t.getMessage());
            }
        });
    }

    public void verifyResetCode(ResetCodeRequest request, APIResponseCallback<TrueBasicResponse> callback) {
        api.verifyResetCode(request).enqueue(new Callback<TrueBasicResponse>() {
            @Override
            public void onResponse(Call<TrueBasicResponse> call, Response<TrueBasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String error = response.errorBody() != null ?
                            response.errorBody().toString() : "Verification code validation failed";
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<TrueBasicResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteAccount(String email, APIResponseCallback<TrueBasicResponse> callback){
        api.deleteAccount(email).enqueue(new Callback<TrueBasicResponse>() {
            @Override
            public void onResponse(Call<TrueBasicResponse> call, Response<TrueBasicResponse> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<TrueBasicResponse> call, Throwable t) {
                callback.onError("Error on deleting the account! "+ t.getMessage());
            }
        });
    }
}