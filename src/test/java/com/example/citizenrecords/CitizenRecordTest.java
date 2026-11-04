package com.example.citizenrecords;

import com.example.citizenrecords.exception.InvalidRecordException;
import com.example.citizenrecords.exception.RecordNotFoundException;
import com.example.citizenrecords.model.BirthRecord;
import com.example.citizenrecords.model.CitizenRecord;
import com.example.citizenrecords.model.NRCApplication;
import com.example.citizenrecords.repository.FileRecordRepository;
import com.example.citizenrecords.repository.InMemoryRecordRepository;
import com.example.citizenrecords.repository.RecordRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive JUnit 5 test suite verifying core Object-Oriented principles:
 * Encapsulation, Inheritance, Polymorphism, Interface contracts, Custom Exceptions
 * (RecordNotFoundException and InvalidRecordException), Collections, and Data Persistence.
 *
 * <p>Arranged following the Arrange-Act-Assert testing pattern (Week 2).
 */
public class CitizenRecordTest {

    // -------------------------------------------------------------
    // Test 1: Encapsulation & Valid Object Creation (Weeks 2 & 4)
    // -------------------------------------------------------------
    @Test
    public void test01_BirthRecordCreationAndEncapsulation() {
        // Arrange
        String name = "Mweembe Phiri";
        LocalDate dob = LocalDate.of(2002, 6, 15);
        String place = "Lusaka";

        // Act
        BirthRecord record = new BirthRecord(name, dob, place);

        // Assert - verified via getters (encapsulated state)
        assertEquals("Mweembe Phiri", record.getFullName());
        assertEquals(dob, record.getDateOfBirth());
        assertEquals("Lusaka", record.getPlaceOfBirth());
        assertNotNull(record.getRecordId(), "Record ID must be generated automatically");
    }

    // -------------------------------------------------------------
    // Test 2: Custom Exception - InvalidRecordException on Future Date (Weeks 4 & 8)
    // -------------------------------------------------------------
    @Test
    public void test02_BirthRecordRejectsFutureDateOfBirth() {
        // Arrange
        LocalDate futureDate = LocalDate.now().plusDays(5);

        // Act & Assert - custom exception thrown on invalid input
        InvalidRecordException ex = assertThrows(InvalidRecordException.class, () -> {
            new BirthRecord("Future Baby", futureDate, "Ndola");
        });
        assertTrue(ex.getMessage().contains("not in the future"));
    }

    // -------------------------------------------------------------
    // Test 3: Custom Exception - InvalidRecordException on Blank Input (Weeks 4 & 8)
    // -------------------------------------------------------------
    @Test
    public void test03_BirthRecordRejectsBlankNameAndPlace() {
        LocalDate validDob = LocalDate.of(2000, 1, 1);

        // Blank name check
        assertThrows(InvalidRecordException.class, () -> {
            new BirthRecord("   ", validDob, "Kitwe");
        });

        // Blank place check
        assertThrows(InvalidRecordException.class, () -> {
            new BirthRecord("John Doe", validDob, "");
        });
    }

    // -------------------------------------------------------------
    // Test 4: Inheritance & Polymorphism (Weeks 6 & 7)
    // -------------------------------------------------------------
    @Test
    public void test04_PolymorphismAndInheritance() {
        // Polymorphic reference: Variable is parent type (CitizenRecord), object on heap is child (BirthRecord)
        CitizenRecord record = new BirthRecord("Chileshe Mulenga", LocalDate.of(1998, 3, 20), "Kabwe");

        // The real object on the heap determines which overridden method executes at runtime
        assertEquals("Birth Record", record.getRecordType());
        assertTrue(record.toString().contains("Place of birth: Kabwe"));
    }

    // -------------------------------------------------------------
    // Test 5: Association & Default State (Weeks 4 & 6)
    // -------------------------------------------------------------
    @Test
    public void test05_NRCApplicationCreationAndAssociation() {
        BirthRecord birth = new BirthRecord("Tembo Banda", LocalDate.of(1995, 11, 10), "Livingstone");

        // Application associates with a birth record via internal ID
        NRCApplication app = new NRCApplication("Tembo Banda", LocalDate.of(1995, 11, 10), birth.getRecordId());

        assertEquals(birth.getRecordId(), app.getBirthRecordId());
        assertEquals("PENDING", app.getStatus(), "Initial status must default to PENDING");
    }

    // -------------------------------------------------------------
    // Test 6: Domain Rules & Status Mutation Validation (Weeks 4 & 8)
    // -------------------------------------------------------------
    @Test
    public void test06_NRCApplicationStatusTransitionsAndValidation() {
        NRCApplication app = new NRCApplication("Kunda Mwila", LocalDate.of(1990, 8, 4), "BIRTH-1234");

        // Valid transitions
        app.setStatus("APPROVED");
        assertEquals("APPROVED", app.getStatus());

        app.setStatus("REJECTED");
        assertEquals("REJECTED", app.getStatus());

        // Invalid status should trigger InvalidRecordException
        assertThrows(InvalidRecordException.class, () -> {
            app.setStatus("CANCELLED");
        });
    }

    // -------------------------------------------------------------
    // Test 7: Testing Through Interface Contract (Week 7)
    // -------------------------------------------------------------
    @Test
    public void test07_InMemoryRepositorySaveAndFind() {
        // Tested through interface reference (RecordRepository)
        RecordRepository repo = new InMemoryRecordRepository();
        BirthRecord birth = new BirthRecord("Bupe Lungu", LocalDate.of(2001, 5, 12), "Chipata");

        repo.save(birth);
        CitizenRecord found = repo.findById(birth.getRecordId());

        assertNotNull(found);
        assertEquals("Bupe Lungu", found.getFullName());
    }

    // -------------------------------------------------------------
    // Test 8: Custom Exception - RecordNotFoundException (Week 8)
    // -------------------------------------------------------------
    @Test
    public void test08_RepositoryThrowsCustomRecordNotFoundException() {
        RecordRepository repo = new InMemoryRecordRepository();

        // Searching for non-existent record must raise custom exception
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> {
            repo.findById("non-existent-uuid-999");
        });

        assertTrue(ex.getMessage().contains("No record found with ID: non-existent-uuid-999"));
    }

    // -------------------------------------------------------------
    // Test 9: Collections Management & Deletion (Week 5)
    // -------------------------------------------------------------
    @Test
    public void test09_RepositoryDeleteById() {
        RecordRepository repo = new InMemoryRecordRepository();
        BirthRecord record1 = new BirthRecord("Alice", LocalDate.of(2000, 1, 1), "Lusaka");
        BirthRecord record2 = new BirthRecord("Bob", LocalDate.of(2001, 2, 2), "Ndola");

        repo.save(record1);
        repo.save(record2);
        assertEquals(2, repo.findAll().size());

        // Delete existing record
        boolean deleted = repo.deleteById(record1.getRecordId());
        assertTrue(deleted);
        assertEquals(1, repo.findAll().size());

        // Deleting non-existent record returns false
        assertFalse(repo.deleteById("unknown-id"));
    }

    // -------------------------------------------------------------
    // Test 10: File Persistence Across Application Restarts (Week 8 & Project Requirement)
    // -------------------------------------------------------------
    @Test
    public void test10_FileRepositoryDataPersistenceAcrossRestarts(@TempDir Path tempDir) {
        File tempFile = tempDir.resolve("records_test.txt").toFile();

        // First session: Save records
        RecordRepository sessionOneRepo = new FileRecordRepository(tempFile);
        BirthRecord birth = new BirthRecord("Chanda Kangwa", LocalDate.of(1996, 7, 22), "Solwezi");
        NRCApplication app = new NRCApplication("Chanda Kangwa", LocalDate.of(1996, 7, 22), birth.getRecordId());
        app.setStatus("APPROVED");

        sessionOneRepo.save(birth);
        sessionOneRepo.save(app);

        // Second session: Simulate restarting the program by initializing a fresh repository with the same file
        RecordRepository sessionTwoRepo = new FileRecordRepository(tempFile);
        List<CitizenRecord> reloadedRecords = sessionTwoRepo.findAll();

        assertEquals(2, reloadedRecords.size(), "All records must persist and reload across restart");

        CitizenRecord reloadedBirth = sessionTwoRepo.findById(birth.getRecordId());
        assertEquals("Chanda Kangwa", reloadedBirth.getFullName());
        assertEquals("Birth Record", reloadedBirth.getRecordType());

        CitizenRecord reloadedApp = sessionTwoRepo.findById(app.getRecordId());
        assertTrue(reloadedApp instanceof NRCApplication);
        assertEquals("APPROVED", ((NRCApplication) reloadedApp).getStatus(), "Application status must persist");
    }
}
