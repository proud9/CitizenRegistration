# Synthetic Citizen Record Manager

A small Java desktop application demonstrating encapsulation, inheritance, polymorphism, interfaces, and associations.

## Requirements

Java Development Kit (JDK) 8 or later.

## Run from a terminal

Open a terminal in this folder and compile the Java sources:

```text
javac -encoding UTF-8 -d out src/main/java/com/example/citizenrecords/exception/RecordNotFoundException.java src/main/java/com/example/citizenrecords/model/CitizenRecord.java src/main/java/com/example/citizenrecords/model/BirthRecord.java src/main/java/com/example/citizenrecords/model/NRCApplication.java src/main/java/com/example/citizenrecords/repository/RecordRepository.java src/main/java/com/example/citizenrecords/repository/InMemoryRecordRepository.java src/main/java/com/example/citizenrecords/ui/RecordManagerFrame.java src/main/java/com/example/citizenrecords/App.java
```

Then launch the window:

```text
java -cp out com.example.citizenrecords.App
```

Use the Birth record tab first. Enter a fictional name, date, and place, then save. The Application tab can use that birth record to create a simulated application. Select a record to read its details. Search by internal ID, delete the selected record, or change an application’s status between PENDING, APPROVED, and REJECTED.

Records are held in memory and disappear when the program closes. Use synthetic data only. IDs are internal references, not NRC numbers. The program does not connect to government systems or issue identity documents.
