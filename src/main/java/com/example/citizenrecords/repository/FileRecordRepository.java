package com.example.citizenrecords.repository;

import com.example.citizenrecords.exception.RecordNotFoundException;
import com.example.citizenrecords.model.BirthRecord;
import com.example.citizenrecords.model.CitizenRecord;
import com.example.citizenrecords.model.NRCApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistent file-backed storage implementation of {@link RecordRepository}.
 *
 * <p><b>Course Project Requirement:</b> Data Persistence (Data must survive restart).
 * Implemented to satisfy the project proposal class design (FileRecordRepository).
 *
 * <p><b>Key OOP Concepts Demonstrated:</b>
 * <ul>
 *   <li><b>Interface Implementation:</b> Implements {@link RecordRepository} contract so the UI / consumer
 *       code remains decoupled from the physical storage mechanism.</li>
 *   <li><b>Polymorphism & Abstraction:</b> Manages collections of abstract {@link CitizenRecord} objects,
 *       dynamically identifying and serializing/deserializing concrete subclasses
 *       ({@link BirthRecord} and {@link NRCApplication}).</li>
 *   <li><b>Encapsulation:</b> Internal in-memory storage (Map) and file I/O operations are private
 *       and synchronized to protect data integrity.</li>
 * </ul>
 *
 * @author Comfort Penyengwe (Student ID: 2410098)
 */
public class FileRecordRepository implements RecordRepository {
    private final File storageFile;
    private final Map<String, CitizenRecord> records = new LinkedHashMap<String, CitizenRecord>();

    /**
     * Default constructor storing data in "data/records.txt".
     */
    public FileRecordRepository() {
        this(new File("data/records.txt"));
    }

    /**
     * Constructor allowing a custom file path for testing or alternative storage locations.
     *
     * @param filePath Path to the persistence file.
     */
    public FileRecordRepository(String filePath) {
        this(new File(filePath));
    }

    /**
     * Constructs repository and automatically loads existing records from the given file.
     *
     * @param storageFile Storage file handle.
     */
    public FileRecordRepository(File storageFile) {
        this.storageFile = storageFile;
        loadRecords();
    }

    @Override
    public synchronized void save(CitizenRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Record cannot be null.");
        }
        records.put(record.getRecordId(), record);
        persistRecords();
    }

    @Override
    public synchronized CitizenRecord findById(String recordId) throws RecordNotFoundException {
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
    public synchronized List<CitizenRecord> findAll() {
        return new ArrayList<CitizenRecord>(records.values());
    }

    @Override
    public synchronized boolean deleteById(String recordId) {
        if (recordId == null) {
            return false;
        }
        boolean removed = records.remove(recordId.trim()) != null;
        if (removed) {
            persistRecords();
        }
        return removed;
    }

    /**
     * Loads and reconstructs records from disk upon application startup.
     */
    private void loadRecords() {
        records.clear();
        if (!storageFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(storageFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                CitizenRecord record = parseRecord(line);
                if (record != null) {
                    records.put(record.getRecordId(), record);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read persistence file: " + e.getMessage());
        }
    }

    /**
     * Writes all active records to disk using standard UTF-8 encoding.
     */
    private synchronized void persistRecords() {
        File parent = storageFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(storageFile), StandardCharsets.UTF_8))) {
            writer.write("# Synthetic Citizen Records Persistence File");
            writer.newLine();
            for (CitizenRecord record : records.values()) {
                writer.write(serializeRecord(record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not write persistence file: " + e.getMessage());
        }
    }

    /**
     * Serializes a record polymorphically based on concrete subclass type.
     */
    private String serializeRecord(CitizenRecord record) {
        if (record instanceof BirthRecord) {
            BirthRecord birth = (BirthRecord) record;
            return String.join("|",
                    "BIRTH",
                    escape(birth.getRecordId()),
                    escape(birth.getFullName()),
                    birth.getDateOfBirth().toString(),
                    escape(birth.getPlaceOfBirth())
            );
        } else if (record instanceof NRCApplication) {
            NRCApplication app = (NRCApplication) record;
            return String.join("|",
                    "NRC_APP",
                    escape(app.getRecordId()),
                    escape(app.getFullName()),
                    app.getDateOfBirth().toString(),
                    escape(app.getBirthRecordId()),
                    escape(app.getStatus())
            );
        }
        throw new IllegalArgumentException("Unknown record type: " + record.getClass().getName());
    }

    /**
     * Parses a stored line back into its concrete domain object.
     */
    private CitizenRecord parseRecord(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 5) {
            return null;
        }

        String type = parts[0];
        String recordId = unescape(parts[1]);
        String fullName = unescape(parts[2]);
        LocalDate dob = LocalDate.parse(parts[3]);

        if ("BIRTH".equals(type) && parts.length >= 5) {
            String placeOfBirth = unescape(parts[4]);
            return new BirthRecord(recordId, fullName, dob, placeOfBirth);
        } else if ("NRC_APP".equals(type) && parts.length >= 6) {
            String birthRecordId = unescape(parts[4]);
            String status = unescape(parts[5]);
            return new NRCApplication(recordId, fullName, dob, birthRecordId, status);
        }
        return null;
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("|", "\\p");
    }

    private String unescape(String value) {
        if (value == null) return "";
        return value.replace("\\p", "|").replace("\\\\", "\\");
    }
}
