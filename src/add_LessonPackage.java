import javax.swing.*;
import java.sql.*;
import java.util.*;

public class add_LessonPackage extends simpleJDBC{

    private static int sqlCode = 0;      // Variable to hold SQLCODE
    private static String sqlState = "00000";  // Variable to hold SQLSTATE

    private static final String lessonPackageTable = "LessonPackage";

    private static final String instructorTable = "Instructor";


    public static boolean addNewLessonPackage(Connection con, String email, double hourlyFee) {

        boolean bool = false;
        // Check if email belongs to an instructor
        if (!isInstructor(con, email)) {
            JOptionPane.showMessageDialog(null, "This email does not belong to a registered instructor.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }



        // Generate new lesson_id
        int lessonId = generateNewLessonId(con) + 1;

        // Insert new lesson package
        bool= insertLessonPackage(con, lessonId, hourlyFee, email);

        return bool;
    }

    private static boolean isInstructor(Connection con, String email) {
        String query = "SELECT COUNT(*) FROM " + instructorTable + " WHERE email = ?";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Prepare the message to display
            String errorMessage = "SQL Error Code: " + sqlCode + "\nSQL State: " + sqlState + "\n" + e.getMessage();

            // Show the error message in a pop-up dialog
            JOptionPane.showMessageDialog(null, errorMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private static int generateNewLessonId(Connection con) {
        String query = "SELECT MAX(lesson_id) FROM " + lessonPackageTable;

        try (Statement statement = con.createStatement()) {

            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Prepare the message to display
            String errorMessage = "SQL Error Code: " + sqlCode + "\nSQL State: " + sqlState + "\n" + e.getMessage();

            // Show the error message in a pop-up dialog
            JOptionPane.showMessageDialog(null, errorMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return 1;
    }

    private static boolean insertLessonPackage(Connection con, int lessonId, double hourlyFee, String instructorEmail) {
        String insertSQL = "INSERT INTO " + lessonPackageTable + " (lesson_id, hourly_fee, instructor_email) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(insertSQL)) {

            preparedStatement.setInt(1, lessonId);
            preparedStatement.setDouble(2, hourlyFee);
            preparedStatement.setString(3, instructorEmail);

            int newRow = preparedStatement.executeUpdate();

            if (newRow != 0) {
                // Display a success message in a pop-up dialog
                JOptionPane.showMessageDialog(null, "New lesson package added successfully. Lesson ID: " + lessonId, "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                // Display a failure message in a pop-up dialog
                JOptionPane.showMessageDialog(null, "Failed to add the new lesson package.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        }catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Prepare the message to display
            String errorMessage = "SQL Error Code: " + sqlCode + "\nSQL State: " + sqlState + "\n" + e.getMessage();

            // Show the error message in a pop-up dialog
            JOptionPane.showMessageDialog(null, errorMessage, "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


    }


}

