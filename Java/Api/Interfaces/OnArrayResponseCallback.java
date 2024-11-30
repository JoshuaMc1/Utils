package com.example.app.Api.Interfaces;

import org.json.JSONArray;

public interface OnArrayResponseCallback {
    void OnSuccess(JSONArray response);

    void OnFailed(int status, String message);
}
