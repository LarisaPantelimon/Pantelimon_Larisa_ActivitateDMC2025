package com.example.API;

public interface APIResponseCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}

