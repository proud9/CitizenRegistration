package com.example.citizenrecords.model;
import java.time.LocalDate;
import java.util.UUID;
public abstract class CitizenRecord {
    private final String recordId = UUID.randomUUID().toString();
    private final String fullName;
    private final LocalDate dateOfBirth;
    protected CitizenRecord(String fullName, LocalDate dateOfBirth) {
        if (fullName == null || fullName.trim().isEmpty()) throw new IllegalArgumentException("Name is required.");
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) throw new IllegalArgumentException("Enter a valid date of birth that is not in the future.");
        this.fullName = fullName.trim(); this.dateOfBirth = dateOfBirth;
    }
    public String getRecordId() { return recordId; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public abstract String getRecordType();
    @Override public String toString() { return getRecordType() + " | Internal ID: " + recordId + " | Name: " + fullName + " | Date of birth: " + dateOfBirth; }
}
