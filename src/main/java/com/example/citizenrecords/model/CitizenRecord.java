package com.example.citizenrecords.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Abstract base class representing a generic synthetic citizen record.
 * Encapsulates common fields and enforces validation across all record types.
 */
public abstract class CitizenRecord {
    private final String recordId;
    private final String fullName;
    private final LocalDate dateOfBirth;

    /**
     * Standard constructor for newly created records; auto-generates a unique internal ID.
     */
    protected CitizenRecord(String fullName, LocalDate dateOfBirth) {
        this(UUID.randomUUID().toString(), fullName, dateOfBirth);
    }

    /**
     * Overloaded constructor supporting data persistence (e.g. {@link com.example.citizenrecords.repository.FileRecordRepository}).
     * Allows reloading existing records with their persistent IDs while maintaining immutability and encapsulation.
     *
     * @param recordId Existing unique record ID.
     * @param fullName Full legal name.
     * @param dateOfBirth Date of birth (cannot be in the future).
     */
    protected CitizenRecord(String recordId, String fullName, LocalDate dateOfBirth) {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID is required.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Enter a valid date of birth that is not in the future.");
        }
        this.recordId = recordId.trim();
        this.fullName = fullName.trim();
        this.dateOfBirth = dateOfBirth;
    }

    public String getRecordId() { return recordId; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public abstract String getRecordType();

    @Override
    public String toString() {
        return getRecordType() + " | Internal ID: " + recordId + " | Name: " + fullName + " | Date of birth: " + dateOfBirth;
    }
}
