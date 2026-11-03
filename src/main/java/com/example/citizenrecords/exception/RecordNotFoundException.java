package com.example.citizenrecords.exception;

/** Raised when a requested in-memory record does not exist. */
public class RecordNotFoundException extends IllegalArgumentException {
    public RecordNotFoundException(String message) { super(message); }
}
