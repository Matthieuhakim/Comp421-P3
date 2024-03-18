import java.sql.*;
import java.util.*;

public class AddEquipmentToLessonPackage {

    private static int sqlCode = 0;      // Variable to hold SQLCODE
    private static String sqlState = "00000";  // Variable to hold SQLSTATE
    private static String includesTable = "Includes";


    private static void addEquipment(Connection con, int lessonId, int equipmentId) throws SQLException {

        PreparedStatement preparedStatement = null;
        try {
            String insertSQL = "INSERT INTO " + includesTable + " (lesson_id, equipment_id) VALUES (?, ?)";
            preparedStatement = con.prepareStatement(insertSQL);

            preparedStatement.setInt(1, lessonId);
            preparedStatement.setInt(2, equipmentId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipment successfully added to lesson package.");
            } else {
                System.out.println("Failed to add equipment to lesson package.");
            }

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
    public static void promptAddEquipmentToLessonPackage(Connection con) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Prompt to see available lesson packages
        System.out.print("Would you like to see the available lesson packages? (y/n) ");
        String response = scanner.next().trim().toLowerCase();

        if (response.equals("y")) {
            System.out.println("Fetching available lesson packages...");
            String lessonQuery = "SELECT lesson_id, 'ID: ' || lesson_id || ', With Instructor: ' || instructor_email AS lesson_info FROM LessonPackage";
            try (Statement lessonStmt = con.createStatement();
                 ResultSet lessonRs = lessonStmt.executeQuery(lessonQuery)) {
                while (lessonRs.next()) {
                    System.out.println(lessonRs.getString("lesson_info"));
                }
            }
        }

        System.out.print("Enter the lesson package ID: ");
        int lessonId = scanner.nextInt();

        // Prompt to see available equipment
        System.out.print("Would you like to see the available equipment? (y/n) ");
        response = scanner.next().trim().toLowerCase();

        if (response.equals("y")) {
            System.out.println("Fetching available equipment...");
            String equipmentQuery = "SELECT equipment_id, 'ID: ' || equipment_id || ', Name: ' || name || ', Type: ' || type || ', Hourly Price: ' || hourly_price AS equipment_info FROM Equipment";

            try (Statement equipmentStmt = con.createStatement();
                 ResultSet equipmentRs = equipmentStmt.executeQuery(equipmentQuery)) {
                while (equipmentRs.next()) {
                    System.out.println(equipmentRs.getString("equipment_info"));
                }
            }
        }

        System.out.print("Enter the equipment ID you want to add to package " + lessonId + ": ");
        int equipmentId = scanner.nextInt();

        addEquipment(con, lessonId, equipmentId);
    }
}

