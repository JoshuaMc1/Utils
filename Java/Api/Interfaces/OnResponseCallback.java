package com.example.app.Api.Interfaces;

import org.json.JSONObject;

public interface OnResponseCallback {
    void OnSuccess(JSONObject response);

    void OnFailed(int status, String message);
}
