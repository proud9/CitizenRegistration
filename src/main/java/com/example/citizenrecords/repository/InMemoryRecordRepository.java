package com.example.citizenrecords.repository;

import com.example.citizenrecords.exception.RecordNotFoundException;
import com.example.citizenrecords.model.CitizenRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Transient in-memory storage; records disappear when the app closes.
 * Demonstrates Map collection usage (Week 5) keyed by unique record ID.
 */
public class InMemoryRecordRepository implements RecordRepository {
    private final Map<String, CitizenRecord> records = new LinkedHashMap<String, CitizenRecord>();

    @Override
    public void save(CitizenRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Record cannot be null.");
        }
        records.put(record.getRecordId(), record);
    }

    @Override
    public CitizenRecord findById(String recordId) throws RecordNotFoundException {
        if (recordId == null || recordId.trim().isEmpty()) {
            throw new IllegalArgumentException("Record ID cannot be null or empty.");
        }
        CitizenRecord record = records.get(recordId.trim());
        if (record == null) {
            throw new RecordNotFoundException("No record found with ID: " + recordId);
        }
        return record;
    }

    @Override
    public List<CitizenRecord> findAll() {
        return new ArrayList<CitizenRecord>(records.values());
    }

    @Override
    public boolean deleteById(String recordId) {
        if (recordId == null) {
            return false;
        }
        return records.remove(recordId.trim()) != null;
    }
}
