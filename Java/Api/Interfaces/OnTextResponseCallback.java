package com.example.app.Api.Interfaces;

public interface OnTextResponseCallback {
    void OnSuccess(String response);

    void OnFailed(int status, String message);
}
