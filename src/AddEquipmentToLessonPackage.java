import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddEquipmentToLessonPackage extends JDialog {
    private final JComboBox<String> lessonPackageComboBox;
    private final JComboBox<String> equipmentComboBox;
    private final JButton addButton;
    private final Connection con;

    public static void promptAddEquipmentToLessonPackage(Connection con) {
        // Call this method to show the dialog
        AddEquipmentToLessonPackage dialog = new AddEquipmentToLessonPackage(null, con);
        dialog.setVisible(true);
    }

    private AddEquipmentToLessonPackage(Frame parent, Connection con) {
        super(parent, "Add Equipment to Lesson Package", true);
        this.con = con;
        setSize(400, 300);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        add(panel, BorderLayout.CENTER);

        lessonPackageComboBox = new JComboBox<>();
        equipmentComboBox = new JComboBox<>();
        addButton = new JButton("Add Equipment");

        panel.add(new JLabel("Select Lesson Package:"));
        panel.add(lessonPackageComboBox);
        panel.add(new JLabel("Select Equipment:"));
        panel.add(equipmentComboBox);
        panel.add(addButton);

        loadLessonPackages();
        loadEquipment();

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEquipment();
            }
        });
    }

    private void loadLessonPackages() {
        try {
            String query = "SELECT lesson_id, 'Lesson ID: ' || lesson_id || ', With Instructor: ' || instructor_email AS lesson_info FROM LessonPackage";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                lessonPackageComboBox.addItem(rs.getString("lesson_info"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEquipment() {
        try {
            String query = "SELECT equipment_id, 'ID: ' || equipment_id || ', Name: ' || name || ', Type: ' || type || ', Hourly Price: ' || hourly_price AS equipment_info FROM Equipment";
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                equipmentComboBox.addItem(rs.getString("equipment_info"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addEquipment() {
        try {
            // Extract the IDs from the selected items
            String lessonInfo = (String) lessonPackageComboBox.getSelectedItem();
            String equipmentInfo = (String) equipmentComboBox.getSelectedItem();
            int lessonId = Integer.parseInt(lessonInfo.split(",")[0].split(": ")[1]);
            int equipmentId = Integer.parseInt(equipmentInfo.split(",")[0].split(": ")[1]);

            String insertSQL = "INSERT INTO Includes (lesson_id, equipment_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
            preparedStatement.setInt(1, lessonId);
            preparedStatement.setInt(2, equipmentId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Equipment successfully added to lesson package.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add equipment to lesson package.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

