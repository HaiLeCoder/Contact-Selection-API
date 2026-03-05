package com.example.contactselection.exception;

/**
 * Thrown when search returns 0 results.
 * Message code: MSG_NO_RESULT
 */
public class NoResultException extends RuntimeException {
    public NoResultException(String message) {
        super(message);
    }
}
