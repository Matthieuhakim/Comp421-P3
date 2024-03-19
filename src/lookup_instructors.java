import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class lookup_instructors extends JFrame {
    private final Connection con;
    private JComboBox<String> stationComboBox;
    private JTextArea instructorsTextArea;
    private JButton lookupButton;
    private static final String assignedTable = "Assigned";
    private static final String skiStationTable = "SkiStation";

    public lookup_instructors(Connection con) {
        this.con = con;
        initializeUI();
        checkTableData(); // Initial diagnostics on the database (to check if its empty and such)
    }

    private void initializeUI() {
        setTitle("Lookup Instructors by Ski Station");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        stationComboBox = new JComboBox<>(fetchSkiStations());
        pane.add(stationComboBox, BorderLayout.NORTH);

        instructorsTextArea = new JTextArea(10, 30);
        instructorsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(instructorsTextArea);
        pane.add(scrollPane, BorderLayout.CENTER);

        lookupButton = new JButton("Lookup Instructors");
        pane.add(lookupButton, BorderLayout.SOUTH);

        lookupButton.addActionListener(e -> lookupInstructorsBySkiStation());
        setVisible(true);
    }

    private Vector<String> fetchSkiStations() {
        Vector<String> skiStations = new Vector<>();
        try (Statement statement = con.createStatement()) {
            String querySQL = "SELECT location_name FROM " + skiStationTable;
            ResultSet rs = statement.executeQuery(querySQL);

            while (rs.next()) {
                skiStations.add(rs.getString("location_name"));
            }
        } catch (SQLException e) {
            showErrorDialog("Error fetching ski stations: " + e.getMessage());
        }
        return skiStations;
    }

    private void lookupInstructorsBySkiStation() {
        String stationName = (String) stationComboBox.getSelectedItem();
        getInstructors(stationName);
    }

    private void getInstructors(String stationName) {
        StringBuilder instructorsBuilder = new StringBuilder("Instructors:\n");
        try (Statement statement = con.createStatement()) {
            String querySQL = "SELECT instructor_email FROM " + assignedTable + " WHERE location_name = '" + stationName + "'";
            ResultSet rs = statement.executeQuery(querySQL);

            int instructorIndex = 1;
            while (rs.next()) {
                String instructor_email = rs.getString(1);
                instructorsBuilder.append(instructorIndex++).append(". ").append(instructor_email).append("\n");
            }

            if (instructorIndex == 1) {
                instructorsBuilder.append("No instructors available at this station.\n");
            }

            instructorsTextArea.setText(instructorsBuilder.toString());
        } catch (SQLException e) {
            showErrorDialog("Error looking up instructors: " + e.getMessage());
        }
    }

    // Method to perform initial diagnostics
    private void checkTableData() {
        checkTableEntries(skiStationTable);
        checkTableEntries(assignedTable);
    }

    // Method to check if tables have entries
    private void checkTableEntries(String tableName) {
        try (Statement statement = con.createStatement()) {
            String querySQL = "SELECT COUNT(*) AS total FROM " + tableName;
            ResultSet rs = statement.executeQuery(querySQL);
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println(tableName + " has " + total + " entries");
                if (total == 0) {
                    JOptionPane.showMessageDialog(this, tableName + " is empty.", "Data Check", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            showErrorDialog("Error checking data for " + tableName + ": " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
