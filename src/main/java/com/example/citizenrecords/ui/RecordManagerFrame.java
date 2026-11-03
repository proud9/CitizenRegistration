package com.example.citizenrecords.ui;

import com.example.citizenrecords.model.BirthRecord;
import com.example.citizenrecords.model.CitizenRecord;
import com.example.citizenrecords.model.NRCApplication;
import com.example.citizenrecords.repository.RecordRepository;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/** Desktop window for creating, searching, and updating synthetic demo records. */
public class RecordManagerFrame extends JFrame {
    private final RecordRepository repository;
    private final DefaultListModel<CitizenRecord> recordListModel = new DefaultListModel<CitizenRecord>();
    private final JList<CitizenRecord> recordList = new JList<CitizenRecord>(recordListModel);
    private final DefaultComboBoxModel<BirthRecord> birthModel = new DefaultComboBoxModel<BirthRecord>();
    private final JComboBox<BirthRecord> birthPicker = new JComboBox<BirthRecord>(birthModel);
    private final JTextField searchField = new JTextField(18);
    private final JTextField appNameField = new JTextField(20);
    private final JTextField appDobField = new JTextField(20);
    private final JTextArea detailsArea = new JTextArea();
    private final JButton approveButton = new JButton("Mark approved");
    private final JButton rejectButton = new JButton("Mark rejected");
    private final JButton pendingButton = new JButton("Mark pending");

    public RecordManagerFrame(RecordRepository repository) {
        super("Synthetic Citizen Record Manager");
        this.repository = repository;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        add(createHeader(), BorderLayout.NORTH);
        add(createForms(), BorderLayout.WEST);
        add(createWorkspace(), BorderLayout.CENTER);
        refreshRecords();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 4, 16));
        JLabel title = new JLabel("Citizen Record Manager");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        header.add(title, BorderLayout.NORTH);
        header.add(new JLabel("Classroom demo only. Enter fictional data. This does not issue identity documents."), BorderLayout.SOUTH);
        return header;
    }

    private JTabbedPane createForms() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(360, 0));
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 8));
        tabs.addTab("Birth record", createBirthForm());
        tabs.addTab("Application", createApplicationForm());
        return tabs;
    }

    private JPanel createBirthForm() {
        JPanel panel = formPanel();
        JTextField name = new JTextField(20);
        JTextField dob = new JTextField(20);
        JTextField place = new JTextField(20);
        int row = 0;
        row = addField(panel, row, "Fictional full name", name);
        row = addField(panel, row, "Date of birth (YYYY-MM-DD)", dob);
        row = addField(panel, row, "Fictional place of birth", place);
        JButton save = new JButton("Save birth record");
        addComponent(panel, row, save, GridBagConstraints.NONE, 0.0);
        addFiller(panel, row + 1);
        save.addActionListener(event -> {
            try {
                BirthRecord record = new BirthRecord(name.getText(), parseDate(dob.getText()), place.getText());
                repository.save(record);
                refreshRecords();
                selectById(record.getRecordId());
                name.setText("");
                dob.setText("");
                place.setText("");
                showInfo("Birth record saved.\nInternal ID: " + record.getRecordId());
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        });
        return panel;
    }

    private JPanel createApplicationForm() {
        JPanel panel = formPanel();
        birthPicker.setRenderer(new BirthRecordRenderer());
        int row = 0;
        row = addField(panel, row, "Link to a saved birth record", birthPicker);
        row = addField(panel, row, "Applicant name", appNameField);
        row = addField(panel, row, "Date of birth (YYYY-MM-DD)", appDobField);
        JLabel help = new JLabel("<html>Status starts as PENDING. This is a simulation only.</html>");
        row = addComponent(panel, row, help, GridBagConstraints.HORIZONTAL, 0.0);
        JButton save = new JButton("Save simulated application");
        addComponent(panel, row, save, GridBagConstraints.NONE, 0.0);
        addFiller(panel, row + 1);
        birthPicker.addActionListener(event -> copyBirthDetails());
        save.addActionListener(event -> saveApplication());
        return panel;
    }

    private JPanel createWorkspace() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 4, 16, 16));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createRecordPanel(), createDetailsPanel());
        split.setResizeWeight(0.58);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        search.add(new JLabel("Find internal ID:"));
        search.add(searchField);
        JButton find = new JButton("Find");
        JButton clear = new JButton("Show all");
        search.add(find);
        search.add(clear);
        find.addActionListener(event -> findRecord());
        clear.addActionListener(event -> {
            searchField.setText("");
            refreshRecords();
        });
        searchField.addActionListener(event -> findRecord());

        recordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordList.setVisibleRowCount(10);
        recordList.setCellRenderer(new RecordListRenderer());
        recordList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateDetails();
            }
        });

        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(recordList), BorderLayout.CENTER);
        panel.add(createListActions(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createListActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton delete = new JButton("Delete selected");
        delete.addActionListener(event -> deleteSelected());
        approveButton.addActionListener(event -> updateSelectedStatus("APPROVED"));
        rejectButton.addActionListener(event -> updateSelectedStatus("REJECTED"));
        pendingButton.addActionListener(event -> updateSelectedStatus("PENDING"));
        actions.add(delete);
        actions.add(approveButton);
        actions.add(rejectButton);
        actions.add(pendingButton);
        return actions;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(new JLabel("Selected record"), BorderLayout.NORTH);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        detailsArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        detailsArea.setText("Save a record, then select it here to see the details.");
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        return panel;
    }

    private void saveApplication() {
        try {
            BirthRecord birth = (BirthRecord) birthPicker.getSelectedItem();
            if (birth == null) {
                throw new IllegalArgumentException("Create a birth record before adding an application.");
            }
            String name = appNameField.getText().trim();
            LocalDate dob = parseDate(appDobField.getText());
            if (!birth.getFullName().equalsIgnoreCase(name) || !birth.getDateOfBirth().equals(dob)) {
                throw new IllegalArgumentException("Applicant name and date of birth must match the selected birth record.");
            }
            NRCApplication application = new NRCApplication(name, dob, birth.getRecordId());
            repository.save(application);
            refreshRecords();
            selectById(application.getRecordId());
            showInfo("Simulated application saved with status PENDING.\nInternal ID: " + application.getRecordId());
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void refreshRecords() {
        String selectedId = recordList.getSelectedValue() == null ? null : recordList.getSelectedValue().getRecordId();
        recordListModel.clear();
        birthModel.removeAllElements();
        List<CitizenRecord> records = repository.findAll();
        for (CitizenRecord record : records) {
            recordListModel.addElement(record);
            if (record instanceof BirthRecord) {
                birthModel.addElement((BirthRecord) record);
            }
        }
        if (selectedId != null) {
            selectById(selectedId);
        }
        if (birthModel.getSize() > 0 && birthPicker.getSelectedIndex() < 0) {
            birthPicker.setSelectedIndex(0);
        }
        updateDetails();
    }

    private void findRecord() {
        String id = searchField.getText().trim();
        if (id.isEmpty()) {
            showError("Enter an internal ID to search.");
            return;
        }
        Optional<CitizenRecord> result = repository.findById(id);
        if (!result.isPresent()) {
            showError("No record found for that internal ID.");
            return;
        }
        refreshRecords();
        selectById(id);
    }

    private void selectById(String id) {
        for (int i = 0; i < recordListModel.size(); i++) {
            if (recordListModel.get(i).getRecordId().equals(id)) {
                recordList.setSelectedIndex(i);
                recordList.ensureIndexIsVisible(i);
                updateDetails();
                return;
            }
        }
    }

    private void deleteSelected() {
        CitizenRecord selected = recordList.getSelectedValue();
        if (selected == null) {
            showError("Select a record to delete.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Delete this record?\n" + selected.getRecordType() + " for " + selected.getFullName(),
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (answer == JOptionPane.YES_OPTION) {
            repository.deleteById(selected.getRecordId());
            refreshRecords();
        }
    }

    private void updateSelectedStatus(String status) {
        CitizenRecord selected = recordList.getSelectedValue();
        if (!(selected instanceof NRCApplication)) {
            showError("Select a simulated application to change its status.");
            return;
        }
        NRCApplication application = (NRCApplication) selected;
        try {
            application.setStatus(status);
            repository.save(application);
            refreshRecords();
            selectById(application.getRecordId());
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateDetails() {
        CitizenRecord selected = recordList.getSelectedValue();
        boolean isApplication = selected instanceof NRCApplication;
        approveButton.setEnabled(isApplication);
        rejectButton.setEnabled(isApplication);
        pendingButton.setEnabled(isApplication);
        if (selected == null) {
            detailsArea.setText(recordListModel.isEmpty()
                    ? "No records yet. Use the Birth record tab to add one."
                    : "Select a record to see its details.");
            return;
        }
        StringBuilder details = new StringBuilder();
        details.append("Type: ").append(selected.getRecordType()).append('\n');
        details.append("Internal ID: ").append(selected.getRecordId()).append('\n');
        details.append("Name: ").append(selected.getFullName()).append('\n');
        details.append("Date of birth: ").append(selected.getDateOfBirth()).append('\n');
        if (selected instanceof BirthRecord) {
            details.append("Place of birth: ").append(((BirthRecord) selected).getPlaceOfBirth()).append('\n');
        }
        if (selected instanceof NRCApplication) {
            NRCApplication application = (NRCApplication) selected;
            details.append("Linked birth record ID: ").append(application.getBirthRecordId()).append('\n');
            details.append("Status: ").append(application.getStatus()).append('\n');
        }
        detailsArea.setText(details.toString());
        detailsArea.setCaretPosition(0);
    }

    private void copyBirthDetails() {
        BirthRecord birth = (BirthRecord) birthPicker.getSelectedItem();
        if (birth != null) {
            appNameField.setText(birth.getFullName());
            appDobField.setText(birth.getDateOfBirth().toString());
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Use a valid date in YYYY-MM-DD format.");
        }
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return panel;
    }

    private int addField(JPanel panel, int row, String label, JComponent field) {
        JLabel caption = new JLabel(label);
        addComponent(panel, row, caption, GridBagConstraints.NONE, 0.0);
        addComponent(panel, row + 1, field, GridBagConstraints.HORIZONTAL, 0.0);
        return row + 2;
    }

    private int addComponent(JPanel panel, int row, JComponent component, int fill, double weighty) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.weighty = weighty;
        constraints.fill = fill;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(4, 2, 4, 2);
        panel.add(component, constraints);
        return row + 1;
    }

    private void addFiller(JPanel panel, int row) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalGlue(), constraints);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Check the entry", JOptionPane.ERROR_MESSAGE);
    }

    private static final class RecordListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof CitizenRecord) {
                CitizenRecord record = (CitizenRecord) value;
                String extra = "";
                if (record instanceof NRCApplication) {
                    extra = " · " + ((NRCApplication) record).getStatus();
                }
                label.setText(record.getRecordType() + extra + " — " + record.getFullName());
            }
            return label;
        }
    }

    private static final class BirthRecordRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            if (value instanceof BirthRecord) {
                BirthRecord record = (BirthRecord) value;
                label.setText(record.getFullName() + " (" + record.getDateOfBirth() + ")");
            } else if (value == null) {
                label.setText("No birth records yet");
            }
            return label;
        }
    }
}
