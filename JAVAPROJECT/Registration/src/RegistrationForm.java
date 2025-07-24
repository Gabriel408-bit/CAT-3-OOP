import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class RegistrationForm {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                initializeDatabase();
                JFrame frame = new JFrame("Registration Forms");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1000, 600);
                frame.setLocationRelativeTo(null);
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("Personal Details", new PersonalDetailsForm().getPanel());
                tabbedPane.addTab("ID Registration", new IDRegistrationForm().getPanel());
                tabbedPane.addTab("View Data", new DataViewForm().getPanel());
                frame.add(tabbedPane);
                frame.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Database connection failed. Please:\n" +
                                "1. Start XAMPP MySQL\n" +
                                "2. Verify MySQL is running on port 3306\n" +
                                "Error: " + ex.getMessage(),
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS registrations (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "mobile VARCHAR(20)," +
                    "gender VARCHAR(10)," +
                    "dob DATE," +
                    "address TEXT," +
                    "contact VARCHAR(20)," +
                    "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.executeUpdate(sql);
        }
    }

    static class PersonalDetailsForm {
        private JPanel panel;
        private JTextField nameField, mobileField;
        private JRadioButton maleRadio, femaleRadio;
        private JComboBox<String> dayCombo, monthCombo, yearCombo;
        private JTextArea addressArea, displayArea;
        private JCheckBox termsCheck;
        private JButton submitButton, resetButton;

        public PersonalDetailsForm() {
            initializeComponents();
            setupLayout();
            setupListeners();
        }

        private void initializeComponents() {
            panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            nameField = new JTextField(20);
            mobileField = new JTextField(20);
            maleRadio = new JRadioButton("Male", true);
            femaleRadio = new JRadioButton("Female");
            ButtonGroup genderGroup = new ButtonGroup();
            genderGroup.add(maleRadio);
            genderGroup.add(femaleRadio);
            String[] days = new String[31];
            for (int i = 0; i < 31; i++)
                days[i] = String.valueOf(i + 1);
            dayCombo = new JComboBox<>(days);
            String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
            monthCombo = new JComboBox<>(months);
            String[] years = new String[100];
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            for (int i = 0; i < 100; i++)
                years[i] = String.valueOf(currentYear - i);
            yearCombo = new JComboBox<>(years);
            addressArea = new JTextArea(5, 20);
            termsCheck = new JCheckBox("Accept Terms And Conditions");
            submitButton = new JButton("Submit");
            resetButton = new JButton("Reset");
            displayArea = new JTextArea(10, 30);
            displayArea.setEditable(false);
        }

        private void setupLayout() {
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(nameField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(new JLabel("Mobile:"), gbc);
            gbc.gridx = 1;
            formPanel.add(mobileField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(new JLabel("Gender:"), gbc);
            gbc.gridx = 1;
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            genderPanel.add(maleRadio);
            genderPanel.add(femaleRadio);
            formPanel.add(genderPanel, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(new JLabel("DOB:"), gbc);
            gbc.gridx = 1;
            JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            dobPanel.add(dayCombo);
            dobPanel.add(monthCombo);
            dobPanel.add(yearCombo);
            formPanel.add(dobPanel, gbc);
            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1;
            formPanel.add(new JScrollPane(addressArea), gbc);
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            formPanel.add(termsCheck, gbc);
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(submitButton);
            buttonPanel.add(resetButton);
            formPanel.add(buttonPanel, gbc);
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    formPanel, new JScrollPane(displayArea));
            splitPane.setResizeWeight(0.5);
            panel.add(splitPane, BorderLayout.CENTER);
        }

        private void setupListeners() {
            submitButton.addActionListener(e -> {
                if (!termsCheck.isSelected()) {
                    JOptionPane.showMessageDialog(panel,
                            "Please accept terms and conditions",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String name = nameField.getText().trim();
                    String mobile = mobileField.getText().trim();
                    String gender = maleRadio.isSelected() ? "Male" : "Female";

                    // Format DOB properly as yyyy-MM-dd
                    int day = Integer.parseInt((String) dayCombo.getSelectedItem());
                    int month = monthCombo.getSelectedIndex() + 1;
                    int year = Integer.parseInt((String) yearCombo.getSelectedItem());
                    String dob = String.format("%04d-%02d-%02d", year, month, day);

                    String address = addressArea.getText().trim();

                    String sql = "INSERT INTO registrations (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.setString(2, mobile.isEmpty() ? null : mobile);
                        stmt.setString(3, gender);
                        stmt.setDate(4, Date.valueOf(dob));
                        stmt.setString(5, address);
                        stmt.executeUpdate();
                    }
                    displayArea.setText("Registration Successful!\n\n");
                    displayArea.append("Name: " + name + "\n");
                    displayArea.append("Mobile: " + mobile + "\n");
                    displayArea.append("Gender: " + gender + "\n");
                    displayArea.append("DOB: " + dob + "\n");
                    displayArea.append("Address: " + address + "\n");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel,
                            "Database error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            resetButton.addActionListener(e -> {
                nameField.setText("");
                mobileField.setText("");
                maleRadio.setSelected(true);
                dayCombo.setSelectedIndex(0);
                monthCombo.setSelectedIndex(0);
                yearCombo.setSelectedIndex(0);
                addressArea.setText("");
                termsCheck.setSelected(false);
                displayArea.setText("");
            });
        }

        public JPanel getPanel() {
            return panel;
        }
    }

    static class IDRegistrationForm {
        private JPanel panel;
        private JTextField idField, nameField, contactField;
        private JRadioButton maleRadio, femaleRadio;
        private JTextArea addressArea, displayArea;
        private JButton exitButton, registerButton;

        public IDRegistrationForm() {
            initializeComponents();
            setupLayout();
            setupListeners();
        }

        private void initializeComponents() {
            panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            idField = new JTextField(20);
            nameField = new JTextField(20);
            maleRadio = new JRadioButton("Male", true);
            femaleRadio = new JRadioButton("Female");
            ButtonGroup genderGroup = new ButtonGroup();
            genderGroup.add(maleRadio);
            genderGroup.add(femaleRadio);
            addressArea = new JTextArea(5, 20);
            contactField = new JTextField(20);
            exitButton = new JButton("Exit");
            registerButton = new JButton("Register");
            displayArea = new JTextArea(10, 30);
            displayArea.setEditable(false);
        }

        private void setupLayout() {
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            // id
            gbc.gridy = 0;
            formPanel.add(new JLabel("ID:"), gbc);
            gbc.gridx = 1;
            formPanel.add(idField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(nameField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(new JLabel("Gender:"), gbc);
            gbc.gridx = 1;
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            genderPanel.add(maleRadio);
            genderPanel.add(femaleRadio);
            formPanel.add(genderPanel, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1;
            formPanel.add(new JScrollPane(addressArea), gbc);
            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(new JLabel("Contact:"), gbc);
            gbc.gridx = 1;
            formPanel.add(contactField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(exitButton);
            buttonPanel.add(registerButton);
            formPanel.add(buttonPanel, gbc);
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    formPanel, new JScrollPane(displayArea));
            splitPane.setResizeWeight(0.5);
            panel.add(splitPane, BorderLayout.CENTER);
        }

        private void setupListeners() {
            registerButton.addActionListener(e -> {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String id = idField.getText().trim();
                    String name = nameField.getText().trim();
                    String gender = maleRadio.isSelected() ? "Male" : "Female";
                    String address = addressArea.getText().trim();
                    String contact = contactField.getText().trim();

                    String sql = "INSERT INTO registrations (name, gender, address, contact) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.setString(2, gender);
                        stmt.setString(3, address);
                        stmt.setString(4, contact.isEmpty() ? null : contact);
                        stmt.executeUpdate();
                    }
                    displayArea.setText("ID Registration Successful!\n\n");
                    displayArea.append("ID: " + id + "\n");
                    displayArea.append("Name: " + name + "\n");
                    displayArea.append("Gender: " + gender + "\n");
                    displayArea.append("Address: " + address + "\n");
                    displayArea.append("Contact: " + contact + "\n");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel,
                            "Database error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            exitButton.addActionListener(e -> System.exit(0));
        }

        public JPanel getPanel() {
            return panel;
        }
    }

    static class DataViewForm {
        private JPanel panel;
        private JTable table;
        private DefaultTableModel tableModel;
        private JButton loadButton;

        public DataViewForm() {
            initializeComponents();
            setupLayout();
            setupListeners();
        }

        private void initializeComponents() {
            panel = new JPanel(new BorderLayout(10, 10));
            tableModel = new DefaultTableModel();
            table = new JTable(tableModel);
            loadButton = new JButton("Load Data");
            // Setup columns that match your table schema
            tableModel.setColumnIdentifiers(new String[] {
                    "ID", "Name", "Mobile", "Gender", "DOB", "Address", "Contact", "Registration Date"
            });
        }

        private void setupLayout() {
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(loadButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        private void setupListeners() {
            loadButton.addActionListener(e -> loadData());
        }

        private void loadData() {
            tableModel.setRowCount(0); // clear existing rows
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                String sql = "SELECT id, name, mobile, gender, dob, address, contact, registration_date FROM registrations";
                ResultSet rs = stmt.executeQuery(sql);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    Object[] row = new Object[8];
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("name");
                    row[2] = rs.getString("mobile");
                    row[3] = rs.getString("gender");
                    Date dob = rs.getDate("dob");
                    row[4] = dob != null ? sdf.format(dob) : "";
                    row[5] = rs.getString("address");
                    row[6] = rs.getString("contact");
                    row[7] = rs.getTimestamp("registration_date");
                    tableModel.addRow(row);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel,
                        "Error loading data: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public JPanel getPanel() {
            return panel;
        }
    }
}
