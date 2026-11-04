package com.example.citizenrecords.model;

import com.example.citizenrecords.exception.InvalidRecordException;
import java.time.LocalDate;

/**
 * Concrete domain model representing a simulated NRC application linked to a birth record.
 */
public class NRCApplication extends CitizenRecord {
    private final String birthRecordId;
    private String status = "PENDING";

    /**
     * Creates a new simulated NRC application linked to a birth record.
     */
    public NRCApplication(String fullName, LocalDate dateOfBirth, String birthRecordId) {
        super(fullName, dateOfBirth);
        if (birthRecordId == null || birthRecordId.trim().isEmpty()) {
            throw new InvalidRecordException("Birth record internal ID is required.");
        }
        this.birthRecordId = birthRecordId.trim();
    }

    /**
     * Overloaded constructor for reloading stored applications and their status from persistent storage.
     */
    public NRCApplication(String recordId, String fullName, LocalDate dateOfBirth, String birthRecordId, String status) {
        super(recordId, fullName, dateOfBirth);
        if (birthRecordId == null || birthRecordId.trim().isEmpty()) {
            throw new InvalidRecordException("Birth record internal ID is required.");
        }
        this.birthRecordId = birthRecordId.trim();
        setStatus(status);
    }

    public String getBirthRecordId() {
        return birthRecordId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || !(status.equals("PENDING") || status.equals("APPROVED") || status.equals("REJECTED"))) {
            throw new InvalidRecordException("Status must be PENDING, APPROVED, or REJECTED.");
        }
        this.status = status;
    }

    @Override
    public String getRecordType() {
        return "Simulated NRC Application";
    }

    @Override
    public String toString() {
        return super.toString() + " | Birth record ID: " + birthRecordId + " | Status: " + status;
    }
}
