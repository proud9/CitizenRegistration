package com.example.citizenrecords.model;
import java.time.LocalDate;
/** Simulated application record; it does not represent an issued identity document. */
public class NRCApplication extends CitizenRecord {
    private final String birthRecordId;
    private String status = "PENDING";
    public NRCApplication(String fullName, LocalDate dateOfBirth, String birthRecordId) {
        super(fullName, dateOfBirth);
        if (birthRecordId == null || birthRecordId.trim().isEmpty()) throw new IllegalArgumentException("Birth record internal ID is required.");
        this.birthRecordId = birthRecordId.trim();
    }
    public String getBirthRecordId() { return birthRecordId; }
    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || !(status.equals("PENDING") || status.equals("APPROVED") || status.equals("REJECTED"))) throw new IllegalArgumentException("Status must be PENDING, APPROVED, or REJECTED.");
        this.status = status;
    }
    @Override public String getRecordType() { return "Simulated NRC Application"; }
    @Override public String toString() { return super.toString() + " | Birth record ID: " + birthRecordId + " | Status: " + status; }
}
