import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class book_reservation extends JFrame {


    private static final String skierTable = "Skier";
    private static final String skiStationTable = "SkiStation";
    private static final String slopeTable = "SkiSlope";
    private static final String timeslotTable = "TimeSlot";
    private static final String openingsTable = "Opening";
    private static final String reservationTable = "Reservation";

    private final Connection con;
    private JPanel mainPanel;
    private JComboBox<String> stationComboBox, slopeComboBox, timeSlotComboBox;
    private JButton showStationsButton;

    private String skierEmail;

    private final CardLayout cardLayout;

    private final JPanel cardsPanel;


    public book_reservation(Connection con) {
        this.con = con;
        setTitle("Book Reservation");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        add(cardsPanel);
        cardsPanel.add(createEmailPanel(), "Email");
        cardsPanel.add(createStationPanel(), "Stations");
        cardsPanel.add(createSlopePanel(), "Slopes");
        cardsPanel.add(createTimeSlotPanel(), "TimeSlots");
        cardsPanel.add(createConfirmationPanel(), "Confirmation");

        cardLayout.show(cardsPanel, "Email");
    }

    private JPanel createEmailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField emailField = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");


        submitButton.addActionListener(e -> {
             skierEmail = emailField.getText().trim();
            if (!skierEmail.isEmpty()) {
                checkSkierExistsAndProceed(skierEmail);
            } else {
                JOptionPane.showMessageDialog(book_reservation.this, "Please enter a valid email.", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            // Just close the popup, don't exit the entire application
            this.dispose();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        panel.add(new JLabel("Enter your email:"), BorderLayout.NORTH);
        panel.add(emailField, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void checkSkierExistsAndProceed(String skierEmail) {
        try {
            String querySQL = "SELECT COUNT(*) AS count FROM " + skierTable + " WHERE email = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(querySQL)) {
                preparedStatement.setString(1, skierEmail);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next() && rs.getInt("count") > 0) {
                    // If skier exists, proceed to the next panel
                    SwingUtilities.invokeLater(() -> cardLayout.show(cardsPanel, "Stations"));
                } else {
                    // If skier does not exist, show error message and stay on the current panel
                    JOptionPane.showMessageDialog(this, "Skier with email " + skierEmail + " does not exist.", "Skier Not Found", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking skier existence: Code: " + e.getErrorCode() + " sqlState: " + e.getSQLState(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel createStationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        stationComboBox = new JComboBox<>();
        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Back");
        JButton cancelButton = new JButton("Cancel");


        // Populate stations
        showStations(con); // Assuming 'con' is accessible here. Otherwise, pass it as a parameter.

        nextButton.addActionListener(e -> {
            String selectedStation = (String) stationComboBox.getSelectedItem();
            if (selectedStation != null) {
                // Save selected station or pass it to the next panel method
                // Proceed to the slopes panel
                cardLayout.show(cardsPanel, "Slopes");
            }
        });

        backButton.addActionListener(e -> {
            // Go back to the previous panel or close the dialog
            cardLayout.show(cardsPanel, "Email");
        });

        cancelButton.addActionListener(e -> {
            // Just close the popup, don't exit the entire application
            this.dispose();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        panel.add(new JLabel("Select a station:"), BorderLayout.NORTH);
        panel.add(stationComboBox, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showStations(Connection con) {
        try {
            String querySQL = "SELECT location_name from " + skiStationTable;
            Statement statement = con.createStatement();
            ResultSet stationResultSet = statement.executeQuery(querySQL);

            stationComboBox.removeAllItems(); // Clear existing items for fresh loading
            while (stationResultSet.next()) {
                String locationName = stationResultSet.getString("location_name");
                stationComboBox.addItem(locationName); // Populate comboBox with station names
            }
            stationResultSet.close();
            statement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSlopePanel() {
        JPanel slopePanel = new JPanel(new BorderLayout());
        slopeComboBox = new JComboBox<>();
        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Back");
        JButton cancelButton = new JButton("Cancel");


        // Assuming stationName is stored or accessible
        String stationName = (String) stationComboBox.getSelectedItem();
        if (stationName != null) {
            showSlopes(con, stationName); // Load slopes for the selected station
        }

        nextButton.addActionListener(e -> {
            String selectedSlope = (String) slopeComboBox.getSelectedItem();
            if (selectedSlope != null) {
                // Extract slope information from the selected item or pass the slope to the next panel
                // Proceed to the time slots panel
                cardLayout.show(cardsPanel, "TimeSlots");
            }
        });

        backButton.addActionListener(e -> {
            // Go back to the station selection panel
            cardLayout.show(cardsPanel, "Stations");
        });

        cancelButton.addActionListener(e -> {
            // Just close the popup, don't exit the entire application
            this.dispose();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        slopePanel.add(new JLabel("Select a slope:"), BorderLayout.NORTH);
        slopePanel.add(slopeComboBox, BorderLayout.CENTER);
        slopePanel.add(buttonPanel, BorderLayout.SOUTH);

        return slopePanel;
    }

    private JPanel createTimeSlotPanel() {
        JPanel timeSlotPanel = new JPanel(new BorderLayout());
        timeSlotComboBox = new JComboBox<>();
        JButton nextButton = new JButton("Next");
        JButton backButton = new JButton("Back");
        JButton cancelButton = new JButton("Cancel");


        // Add action listeners
        nextButton.addActionListener(e -> {
            String selectedItem = (String) timeSlotComboBox.getSelectedItem();
            if (selectedItem != null) {
                // Extract time slot information from the selected item
                // Possibly, show a confirmation panel or process the reservation
                cardLayout.show(cardsPanel, "Confirmation"); // Transition to confirmation or next step
            }
        });

        backButton.addActionListener(e -> {
            // Go back to the slope selection panel
            cardLayout.show(cardsPanel, "Slopes");
        });

        cancelButton.addActionListener(e -> {
            // Just close the popup, don't exit the entire application
            this.dispose();
        });

        // Assuming stationName and slopeNo are stored or accessible when this panel is prepared to be shown
        String stationName = (String) stationComboBox.getSelectedItem(); // Get selected station name
        Integer slopeNo = slopeComboBox.getSelectedIndex() + 1;

        if (stationName != null && slopeNo != null) {
            showTimeSlots(con, stationName, slopeNo); // Populate timeSlotComboBox with time slots
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(cancelButton);

        timeSlotPanel.add(new JLabel("Select a time slot:"), BorderLayout.NORTH);
        timeSlotPanel.add(timeSlotComboBox, BorderLayout.CENTER);
        timeSlotPanel.add(buttonPanel, BorderLayout.SOUTH);

        return timeSlotPanel;
    }


    private JPanel createConfirmationPanel() {
        JPanel confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new BoxLayout(confirmationPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment

        JLabel stationLabel = new JLabel("Station: " + stationComboBox.getSelectedItem());
        JLabel slopeLabel = new JLabel("Slope: " + slopeComboBox.getSelectedItem());
        JLabel timeSlotLabel = new JLabel("Time Slot: " + timeSlotComboBox.getSelectedItem());

        JButton confirmButton = new JButton("Confirm Reservation");
        JButton backButton = new JButton("Back");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            try {
                int slopeNo = getSlopeNoFromSelectedItem((String) slopeComboBox.getSelectedItem());
                int timeslotId = getTimeSlotIdFromSelectedItem((String) timeSlotComboBox.getSelectedItem());

                insertReservation(con, skierEmail, (String) stationComboBox.getSelectedItem(), slopeNo, timeslotId);
                //JOptionPane.showMessageDialog(this, "Reservation confirmed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close the window after confirming
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to confirm reservation. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            // Switch back to the previous panel
            cardLayout.show(cardsPanel, "TimeSlots"); // Assuming the last panel before confirmation is the TimeSlots panel
        });

        cancelButton.addActionListener(e -> {
            // Just close the popup, don't exit the entire application
            this.dispose(); // This closes the current window but not the entire application
        });

        // Adding components to the panel
        confirmationPanel.add(stationLabel);
        confirmationPanel.add(slopeLabel);
        confirmationPanel.add(timeSlotLabel);
        confirmationPanel.add(confirmButton);
        confirmationPanel.add(backButton);
        confirmationPanel.add(cancelButton);

        return confirmationPanel;
    }


    private int getSlopeNoFromSelectedItem(String selectedItem) {
        try {
            String slopeNoStr = selectedItem.substring(selectedItem.indexOf("Slope No: ") + "Slope No: ".length(),
                    selectedItem.indexOf(", Difficulty"));
            return Integer.parseInt(slopeNoStr.trim()); // Convert the string number into an integer
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Failed to parse slope number: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return -1; // Return an invalid number to indicate a parsing error
        }
    }


    private int getTimeSlotIdFromSelectedItem(String selectedItem) {
        try {
            String idStr = selectedItem.substring(selectedItem.indexOf("ID: ") + "ID: ".length(),
                    selectedItem.indexOf(", Date"));
            return Integer.parseInt(idStr.trim()); // Convert the string number into an integer
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Failed to parse timeslot ID: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return -1; // Return an invalid number to indicate a parsing error
        }
    }




    private void showSlopes(Connection con, String stationName) {
        slopeComboBox.removeAllItems(); // Clear previous items
        try {
            String query = "SELECT slope_no, difficulty, hourly_price FROM " + slopeTable + " WHERE location_name = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, stationName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = "Slope No: " + rs.getInt("slope_no") + ", Difficulty: " + rs.getInt("difficulty") + ", Hourly Price: " + rs.getFloat("hourly_price");
                slopeComboBox.addItem(item);
            }
            if (slopeComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No slopes available at this station.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTimeSlots(Connection con, String stationName, Integer slopeNo) {
        timeSlotComboBox.removeAllItems(); // Clear previous items
        try {
            String query = "SELECT ts.timeslot_id, ts.date, ts.start_time, ts.end_time FROM " + openingsTable + " op, " + timeslotTable + " ts WHERE op.timeslot_id = ts.timeslot_id AND op.location_name = ? AND op.slope_no = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, stationName);
            ps.setInt(2, slopeNo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = "ID: " + rs.getInt("timeslot_id") + ", Date: " + rs.getString("date") + ", Time: " + rs.getString("start_time") + " to " + rs.getString("end_time");
                timeSlotComboBox.addItem(item);
            }
            if (timeSlotComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, "No time slots available at this station.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertReservation(Connection con, String skierEmail, String locationName, Integer slopeNo, Integer timeslotId) throws SQLException {
        int reservationId = generateUniqueReservationId(con);
        if (reservationId == -1) return; // Error generating unique ID


        String insertSQL = "INSERT INTO " + reservationTable + " (reservation_id, skier_email, location_name, slope_no, timeslot_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = con.prepareStatement(insertSQL)) {
            preparedStatement.setInt(1, reservationId);
            preparedStatement.setString(2, skierEmail);
            preparedStatement.setString(3, locationName);
            preparedStatement.setInt(4, slopeNo);
            preparedStatement.setInt(5, timeslotId);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Reservation booked successfully! Reservation ID: " + reservationId, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to book the reservation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static boolean isReservationIdExists(Connection con, int reservationId) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + reservationTable + " WHERE reservation_id = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, reservationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    private static int generateUniqueReservationId(Connection con) throws SQLException {
        int reservationId;
        Random random = new Random();

        do {
            // Generate a random integer ID
            reservationId = random.nextInt(Integer.MAX_VALUE);
        } while (isReservationIdExists(con, reservationId)); // Check if it already exists in the database

        return reservationId;
    }
}
