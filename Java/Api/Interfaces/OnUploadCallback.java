package com.example.app.Api.Interfaces;

public interface OnUploadCallback {
    void OnSuccess(String response);

    void OnFailed(String error);
}
