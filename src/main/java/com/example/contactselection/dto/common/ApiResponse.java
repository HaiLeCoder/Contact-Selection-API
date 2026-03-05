package com.example.contactselection.dto.common;

import lombok.Data;

/**
 * Base response DTO – tương đương CommonResultDto trong tài liệu.
 */
@Data
public class ApiResponse<T> {

    private boolean success;
    private String  message;
    private T       data;
    private String  errorCode;

    /** Factory: success */
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(true);
        res.setData(data);
        return res;
    }

    /** Factory: success with message */
    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> res = ok(data);
        res.setMessage(message);
        return res;
    }

    /** Factory: error */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setSuccess(false);
        res.setMessage(message);
        res.setErrorCode(errorCode);
        return res;
    }
}
