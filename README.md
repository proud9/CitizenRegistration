# Synthetic Citizen Record Manager

A Java desktop application demonstrating encapsulation, inheritance, polymorphism, interfaces, and associations.

## Requirements

Java Development Kit (JDK) 8 or later.

## Classes & Architecture

- **`CitizenRecord`** *(abstract)*: Base class encapsulating shared identity fields (`recordId`, `fullName`, `dateOfBirth`). Enforces validation rules.
- **`BirthRecord`**: Subclass of `CitizenRecord` adding fictional birth place details.
- **`NRCApplication`**: Subclass of `CitizenRecord` linking to a birth record and maintaining status (`PENDING`, `APPROVED`, `REJECTED`).
- **`RecordRepository`** *(interface)*: Defines the abstraction for CRUD storage operations (`save`, `findById`, `findAll`, `deleteById`).
- **`FileRecordRepository`**: Persistent implementation of `RecordRepository` saving and reloading records to/from `data/records.txt` so records survive restarts.
- **`InMemoryRecordRepository`**: Transient repository implementation for in-memory testing.
- **`RecordManagerFrame`**: Swing-based desktop UI for clerks to create, search, review, and manage records.
- **`App`**: Main entry point wiring `FileRecordRepository` into the UI.

## Run from a terminal

Open a terminal in this folder and compile the Java sources:

```powershell
javac -encoding UTF-8 -d out src/main/java/com/example/citizenrecords/exception/*.java src/main/java/com/example/citizenrecords/model/*.java src/main/java/com/example/citizenrecords/repository/*.java src/main/java/com/example/citizenrecords/ui/*.java src/main/java/com/example/citizenrecords/App.java
```

Then launch the application:

```powershell
java -cp out com.example.citizenrecords.App
```

## Features & Usage

1. **Birth Records**: Enter a fictional name, date of birth, and place, then click **Save birth record**.
2. **NRC Applications**: Select a birth record to submit a simulated identity application.
3. **Data Persistence**: Records are saved locally to `data/records.txt` and automatically reload whenever the program is reopened.
4. **Record Management**: Search by internal ID, view formatted details, update application approval status, or delete records.

*Note: All data is fictional and synthetic for academic demonstration. The system does not connect to government databases or issue valid identity documents.*
