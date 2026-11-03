package com.example.citizenrecords.repository;
import com.example.citizenrecords.model.CitizenRecord;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/** Classroom demo storage; records disappear when the app closes. */
public class InMemoryRecordRepository implements RecordRepository {
    private final Map<String, CitizenRecord> records = new LinkedHashMap<String, CitizenRecord>();
    @Override public void save(CitizenRecord record) {
        if (record == null) throw new IllegalArgumentException("Record cannot be null.");
        records.put(record.getRecordId(), record);
    }
    @Override public Optional<CitizenRecord> findById(String recordId) { return Optional.ofNullable(records.get(recordId)); }
    @Override public List<CitizenRecord> findAll() { return new ArrayList<CitizenRecord>(records.values()); }
    @Override public boolean deleteById(String recordId) { return records.remove(recordId) != null; }
}
