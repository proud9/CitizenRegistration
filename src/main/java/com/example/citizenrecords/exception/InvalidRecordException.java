package com.example.citizenrecords.exception;

/**
 * Custom domain exception thrown when citizen record data violates business validation rules
 * (e.g. blank names, future birth dates, empty place of birth, or invalid application statuses).
 *
 * <p>Extends {@link IllegalArgumentException} so it serves as an unchecked domain exception
 * while adhering to the course project proposal specifications.
 *
 * @author Comfort Penyengwe (Student ID: 2410098)
 */
public class InvalidRecordException extends IllegalArgumentException {
    public InvalidRecordException(String message) {
        super(message);
    }
}
