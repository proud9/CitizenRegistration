package com.example.citizenrecords.model;

import com.example.citizenrecords.exception.InvalidRecordException;
import java.time.LocalDate;

/**
 * Concrete domain model for synthetic birth records.
 */
public class BirthRecord extends CitizenRecord {
    private final String placeOfBirth;

    /**
     * Creates a new birth record with an auto-generated internal ID.
     */
    public BirthRecord(String fullName, LocalDate dateOfBirth, String placeOfBirth) {
        super(fullName, dateOfBirth);
        if (placeOfBirth == null || placeOfBirth.trim().isEmpty()) {
            throw new InvalidRecordException("Place of birth is required.");
        }
        this.placeOfBirth = placeOfBirth.trim();
    }

    /**
     * Overloaded constructor for reloading stored records from persistent storage.
     */
    public BirthRecord(String recordId, String fullName, LocalDate dateOfBirth, String placeOfBirth) {
        super(recordId, fullName, dateOfBirth);
        if (placeOfBirth == null || placeOfBirth.trim().isEmpty()) {
            throw new InvalidRecordException("Place of birth is required.");
        }
        this.placeOfBirth = placeOfBirth.trim();
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    @Override
    public String getRecordType() {
        return "Birth Record";
    }

    @Override
    public String toString() {
        return super.toString() + " | Place of birth: " + placeOfBirth;
    }
}
