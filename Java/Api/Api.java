package com.example.mylist_betalist.Api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mylist_betalist.Api.Interfaces.OnArrayResponseCallback;
import com.example.mylist_betalist.Api.Interfaces.OnResponseCallback;
import com.example.mylist_betalist.Api.Interfaces.OnTextResponseCallback;
import com.example.mylist_betalist.Api.Interfaces.OnUploadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

class Api {
    public final int GET = Request.Method.GET;
    public final int POST = Request.Method.POST;
    public final int PUT = Request.Method.PUT;
    public final int DELETE = Request.Method.DELETE;
    public final int PATCH = Request.Method.PATCH;
    private final Map<String, String> headers;
    private RequestQueue queue;

    Api(@Nullable String token) {
        this.headers = new HashMap<>();

        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        if (token != null) {
            headers.put("Authorization", "Bearer " + token);
        }
    }

    /**
     * Returns a RequestQueue object that can be used to send requests to the
     * API. The RequestQueue is created with the application context, so the
     * requests will continue to be processed even if the user navigates away
     * from the activity that initiated the request.
     *
     * @param context The context to use to create the RequestQueue.
     * @return The RequestQueue object that can be used to send requests to the
     * API.
     */
    private RequestQueue getQueue(@NonNull Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }

        return queue;
    }

    /**
     * Sends a request to the given URL with the given HTTP method and returns
     * the response as a string. The response will be passed to the callback
     * method when the request is complete.
     *
     * @param ctx      The context to use to create the RequestQueue.
     * @param uri      The URL of the resource to access.
     * @param method   The HTTP method to use to access the resource.
     * @param callback The callback to invoke with the response.
     * @param tag      The tag to associate with the request, or null if no tag
     *                 is desired.
     */
    void requestPlainText(@NonNull Context ctx, String uri, int method, @NonNull OnTextResponseCallback callback, @Nullable Object tag) {
        queue = getQueue(ctx);

        StringRequest request = new StringRequest(method, uri, callback::OnSuccess, error -> callback.OnFailed(handleVolleyStatus(error), handleVolleyError(error))) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        if (tag != null) request.setTag(tag);

        queue.add(request);
    }

    /**
     * Sends a request to the given URL with the given HTTP method and returns
     * the response as a JSONObject. The response will be passed to the callback
     * method when the request is complete.
     *
     * @param ctx      The context to use to create the RequestQueue.
     * @param uri      The URL of the resource to access.
     * @param method   The HTTP method to use to access the resource.
     * @param data     The data to send with the request, or null if no data is
     *                 desired.
     * @param headers  The headers to send with the request, or null if the
     *                 default headers should be used.
     * @param callback The callback to invoke with the response.
     * @param tag      The tag to associate with the request, or null if no tag
     *                 is desired.
     */
    void requestObject(@NonNull Context ctx, String uri, int method, @Nullable JSONObject data, @Nullable Map<String, String> headers, @NonNull OnResponseCallback callback, @Nullable Object tag) {
        queue = getQueue(ctx);

        JsonObjectRequest request = new JsonObjectRequest(method, uri, data, callback::OnSuccess, error -> callback.OnFailed(handleVolleyStatus(error), handleVolleyError(error))) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                return headers != null ? headers : Api.this.headers;
            }
        };

        if (tag != null) request.setTag(tag);

        queue.add(request);
    }

    /**
     * Sends a request to the given URL with the given HTTP method and returns
     * the response as a JSONArray. The response will be passed to the callback
     * method when the request is complete.
     *
     * @param ctx      The context to use to create the RequestQueue.
     * @param uri      The URL of the resource to access.
     * @param method   The HTTP method to use to access the resource.
     * @param data     The data to send with the request, or null if no data is
     *                 desired.
     * @param headers  The headers to send with the request, or null if the
     *                 default headers should be used.
     * @param callback The callback to invoke with the response.
     * @param tag      The tag to associate with the request, or null if no tag
     *                 is desired.
     */
    void requestArray(@NonNull Context ctx, String uri, int method, @Nullable JSONArray data, @Nullable Map<String, String> headers, @NonNull OnArrayResponseCallback callback, @Nullable Object tag) {
        queue = getQueue(ctx);

        JsonArrayRequest request = new JsonArrayRequest(method, uri, data, callback::OnSuccess, error -> callback.OnFailed(handleVolleyStatus(error), handleVolleyError(error))) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() {
                return headers != null ? headers : Api.this.headers;
            }
        };

        if (tag != null) request.setTag(tag);

        queue.add(request);
    }

    /**
     * Uploads an image file to the specified URL using a multipart request. The response
     * from the server will be passed to the callback method upon completion.
     *
     * @param ctx      The context to use to create the RequestQueue.
     * @param url      The URL to which the image file is to be uploaded.
     * @param params   The parameters to include in the multipart request.
     * @param fileName The name of the file to be uploaded.
     * @param fileData The byte array representing the file data.
     * @param mimeType The MIME type of the file.
     * @param callback The callback to invoke with the result of the upload.
     * @param tag      The tag to associate with the request, or null if no tag is desired.
     */
    void uploadImage(@NonNull Context ctx, String url, @NonNull Map<String, String> params, @NonNull String fileName, @NonNull byte[] fileData, @NonNull String mimeType, @NonNull OnUploadCallback callback, @Nullable Object tag) {
        queue = getQueue(ctx);

        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, url, response -> {
            try {
                String result = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                callback.OnSuccess(result);
            } catch (UnsupportedEncodingException e) {
                callback.OnFailed("Encoding error: " + e.getMessage());
            }
        }, error -> {
            callback.OnFailed("Error (" + handleVolleyStatus(error) + "): " + handleVolleyError(error));
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> data = new HashMap<>();
                data.put("file", new DataPart(fileName, fileData, mimeType));
                return data;
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        if (tag != null) multipartRequest.setTag(tag);

        queue.add(multipartRequest);
    }

    /**
     * Cancels all requests in the queue with the given tag. If the tag is null,
     * all requests in the queue will be cancelled.
     *
     * @param tag The tag to use to identify the requests to cancel, or null to
     *            cancel all requests in the queue.
     */
    void cancelAllRequests(@NonNull Object tag) {
        if (queue != null) {
            queue.cancelAll(tag);
        }
    }

    /**
     * Returns the HTTP status code of the given VolleyError, or -1 if the
     * VolleyError does not contain a network response.
     *
     * @param error The VolleyError to get the status code from.
     * @return The HTTP status code of the given VolleyError, or -1 if the
     * VolleyError does not contain a network response.
     */
    private int handleVolleyStatus(@NonNull VolleyError error) {
        return error.networkResponse != null ? error.networkResponse.statusCode : -1;
    }

    /**
     * Returns the error message from the given VolleyError, or "Unknown error
     * occurred." if the VolleyError does not contain a network response.
     *
     * @param error The VolleyError to get the error message from.
     * @return The error message from the given VolleyError, or "Unknown error
     * occurred." if the VolleyError does not contain a network response.
     */
    @NonNull
    private String handleVolleyError(@NonNull VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            return new String(error.networkResponse.data);
        }

        return "Unknown error occurred.";
    }
}
