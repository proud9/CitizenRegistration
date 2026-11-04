package com.example.citizenrecords.repository;

import com.example.citizenrecords.exception.RecordNotFoundException;
import com.example.citizenrecords.model.CitizenRecord;
import java.util.List;

/**
 * Storage contract for synthetic citizen records.
 * Demonstrates the Interface concept (Week 7) defining what operations
 * must be performed without exposing how they are stored.
 */
public interface RecordRepository {
    void save(CitizenRecord record);
    CitizenRecord findById(String recordId) throws RecordNotFoundException;
    List<CitizenRecord> findAll();
    boolean deleteById(String recordId);
}
