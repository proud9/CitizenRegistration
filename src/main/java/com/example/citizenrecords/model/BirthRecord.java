package com.example.citizenrecords.model;
import java.time.LocalDate;
public class BirthRecord extends CitizenRecord {
    private final String placeOfBirth;
    public BirthRecord(String fullName, LocalDate dateOfBirth, String placeOfBirth) {
        super(fullName, dateOfBirth);
        if (placeOfBirth == null || placeOfBirth.trim().isEmpty()) throw new IllegalArgumentException("Place of birth is required.");
        this.placeOfBirth = placeOfBirth.trim();
    }
    public String getPlaceOfBirth() { return placeOfBirth; }
    @Override public String getRecordType() { return "Birth Record"; }
    @Override public String toString() { return super.toString() + " | Place of birth: " + placeOfBirth; }
}
