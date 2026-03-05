package com.example.contactselection.exception;

/**
 * Thrown when user selects more than 1 contact but the caller
 * screen only allows single selection (kindRef != 1).
 * Message code: MSG_SELECT_ONE
 */
public class SelectionLimitException extends RuntimeException {
    public SelectionLimitException(String message) {
        super(message);
    }
}
