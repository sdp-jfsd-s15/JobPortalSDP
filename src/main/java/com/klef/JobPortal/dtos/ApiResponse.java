package com.klef.JobPortal.dtos;

public class ApiResponse {

    private boolean success;   // Indicates if the request was successful
    private String message;    // A message providing more context
    private Object data;       // Optional data to return with the response

    // Constructor for success response with message only
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Constructor for success response with message and additional data
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

