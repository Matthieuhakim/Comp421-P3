import java.sql.*;
import java.util.*;

public class add_LessonPackage {

    private static int sqlCode = 0;      // Variable to hold SQLCODE
    private static String sqlState = "00000";  // Variable to hold SQLSTATE

    private static String lessonPackageTable = "LessonPackage";

    private static String instructorTable = "Instructor";


    public static void addNewLessonPackage(Connection con) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your instructor email: ");
        String email = scanner.nextLine();

        // Check if email belongs to an instructor
        if (!isInstructor(con, email)) {
            System.out.println("This email does not belong to a registered instructor.");
            return;
        }

        System.out.print("Enter hourly fee for the new lesson package: ");
        double hourlyFee = scanner.nextDouble();

        // Generate new lesson_id
        int lessonId = generateNewLessonId(con) + 1;

        // Insert new lesson package
        insertLessonPackage(con, lessonId, hourlyFee, email);
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

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
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

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        return 1;
    }

    private static void insertLessonPackage(Connection con, int lessonId, double hourlyFee, String instructorEmail) {
        String insertSQL = "INSERT INTO " + lessonPackageTable + " (lesson_id, hourly_fee, instructor_email) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(insertSQL)) {

            preparedStatement.setInt(1, lessonId);
            preparedStatement.setDouble(2, hourlyFee);
            preparedStatement.setString(3, instructorEmail);

            int newRow = preparedStatement.executeUpdate();

            if (newRow != 0) {
                System.out.println("New lesson package added successfully. Lesson ID: " + lessonId);
            } else {
                System.out.println("Failed to add the new lesson package.");
            }

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }


}

