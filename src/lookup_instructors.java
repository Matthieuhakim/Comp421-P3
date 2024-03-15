import java.sql.*;
import java.util.*;

public class lookup_instructors {

    private static int sqlCode = 0;      // Variable to hold SQLCODE
    private static String sqlState = "00000";  // Variable to hold SQLSTATE
    private static String assignedTable = "Assigned";
    private static String skiStationTable = "SkiStation";


    private static void get_instructors(Connection con, String stationName) throws SQLException {

        Statement statement = con.createStatement();
        try {
            String querySQL = "SELECT instructor_email from " + assignedTable + " WHERE location_name = '" + stationName + "'";
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            System.out.println("Instructors: \n");

            int instructorIndex = 1;
            while (rs.next()) {
                String instructor_email = rs.getString(1);
                System.out.println(instructorIndex + ". " + instructor_email);
                instructorIndex++;
            }

            if (instructorIndex == 1) {
                System.out.println("No instructors available at this station.");
            }
            System.out.println();
            rs.close();


        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }

        statement.close();
    }

    public static void lookupInstructorsBySkiStation(Connection con) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Fetching available ski stations...");

        Statement statement = con.createStatement();
        try {
            String querySQL = "SELECT location_name from " + skiStationTable;
            java.sql.ResultSet stationResultSet = statement.executeQuery(querySQL);
            System.out.println("Select a Ski Station:");

            int stationIndex = 1;
            List<String> choices = new ArrayList<>();
            while (stationResultSet.next()) {
                String skiStation = stationResultSet.getString("location_name");
                choices.add(skiStation);
                System.out.println(stationIndex + ". " + skiStation);
                stationIndex++;
            }
            //  Final option to go back to main menu
            System.out.println(stationIndex + ". Go back to main menu");


            System.out.print("Enter your choice: ");
            int stationChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (stationChoice >= 1 && stationChoice < stationIndex) {
                String selectedSkiStation = choices.get(stationChoice - 1);
                get_instructors(con, selectedSkiStation);
            }
            else if (stationChoice != stationIndex){
                System.out.println("Invalid station choice. \n");
            }
            stationResultSet.close();
            statement.close();


        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
    }
}
