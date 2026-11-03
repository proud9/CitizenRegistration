package com.example.citizenrecords;

import com.example.citizenrecords.repository.InMemoryRecordRepository;
import com.example.citizenrecords.repository.RecordRepository;
import com.example.citizenrecords.ui.RecordManagerFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecordRepository repository = new InMemoryRecordRepository();
            RecordManagerFrame frame = new RecordManagerFrame(repository);
            frame.setVisible(true);
        });
    }
}
