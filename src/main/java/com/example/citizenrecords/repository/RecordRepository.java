package com.example.citizenrecords.repository;
import com.example.citizenrecords.model.CitizenRecord;
import java.util.List;
import java.util.Optional;
public interface RecordRepository {
    void save(CitizenRecord record);
    Optional<CitizenRecord> findById(String recordId);
    List<CitizenRecord> findAll();
    boolean deleteById(String recordId);
}
