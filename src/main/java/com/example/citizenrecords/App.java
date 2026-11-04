package com.example.citizenrecords;

import com.example.citizenrecords.repository.FileRecordRepository;
import com.example.citizenrecords.repository.RecordRepository;
import com.example.citizenrecords.ui.RecordManagerFrame;
import javax.swing.SwingUtilities;

/**
 * Main application entry point for the Synthetic Citizen Record Manager desktop interface.
 * Initializes the persistent file repository and launches the graphical interface.
 */
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Uses FileRecordRepository to ensure records survive application restarts
            RecordRepository repository = new FileRecordRepository("data/records.txt");
            RecordManagerFrame frame = new RecordManagerFrame(repository);
            frame.setVisible(true);
        });
    }
}
