import java.sql.*;
import java.util.*;

public class book_reservation {

    private static int sqlCode = 0;      // Variable to hold SQLCODE
    private static String sqlState = "00000";  // Variable to hold SQLSTATE

    private static String skierTable = "Skier";
    private static String skiStationTable = "SkiStation";
    private static String slopeTable = "SkiSlope";
    private static String timeslotTable = "TimeSlot";
    private static String openingsTable = "Opening";
    private static String reservationTable = "Reservation";

    public static void execute(Connection con) throws SQLException {

        //Ask for skier email
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your email: ");
        String skier_email = scanner.nextLine();

        //Check if skier exists
        if (!checkSkierExists(con, skier_email)) {
            return;
        }

        //Show Ski Stations
        String station_name = showStations(con);
        if (station_name == null) {
            return;
        }

        Integer slope_no = showSlopes(con, station_name);
        if (slope_no == null) {
            return;
        }

        Integer timeslot_id = showTimeSlots(con, station_name, slope_no);
        if (timeslot_id == null) {
            return;
        }

        //Insert reservation
        insertReservation(con, skier_email, station_name, slope_no, timeslot_id);

    }

    private static boolean checkSkierExists(Connection con, String skier_email) throws SQLException{
        Statement statement = con.createStatement();
        try {
            String querySQL = "SELECT * from " + skierTable + " WHERE email = '" + skier_email + "'";
            java.sql.ResultSet rs = statement.executeQuery(querySQL);
            if (!rs.next()) {
                System.out.println("Skier with email " + skier_email + " does not exist.");
                return false;
            }
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
        return true;
    }

    private static String showStations(Connection con) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        Statement statement = con.createStatement();
        try {
            String querySQL = "SELECT location_name from " + skiStationTable;
            java.sql.ResultSet stationResultSet = statement.executeQuery(querySQL);
            System.out.println("Select a Ski Station:");

            int stationIndex = 1;
            List<String> choices = new ArrayList<>();
            while (stationResultSet.next()) {
                String location_name = stationResultSet.getString(1);
                System.out.println(stationIndex + ". " + location_name);
                choices.add(location_name);
                stationIndex++;
            }

            if (stationIndex == 1) {
                System.out.println("No ski stations available.");
                return null;
            }

            System.out.print("Please Enter Your Option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (option < 1 || option >= stationIndex) {
                System.out.println("Invalid option. Please choose again.\n");
                return null;
            }

            return choices.get(option - 1);

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        statement.close();
        return null;
    }

    private static Integer showSlopes(Connection con, String stationName) throws SQLException{
        Scanner scanner = new Scanner(System.in);
        Statement statement = con.createStatement();

        try {
            String querySQL = "SELECT slope_no, difficulty, hourly_price from " + slopeTable + " WHERE location_name = '" + stationName + "'";
            java.sql.ResultSet slopeResultSet = statement.executeQuery(querySQL);
            System.out.println("Select a Slope:");

            int slopeIndex = 1;
            List<Integer> choices = new ArrayList<>();
            while (slopeResultSet.next()) {
                Integer slope_no = slopeResultSet.getInt(1);
                Integer difficulty = slopeResultSet.getInt(2);
                Float hourly_price = slopeResultSet.getFloat(3);
                System.out.println(slopeIndex + ". " + slope_no + " - Difficulty: " + difficulty + " - Hourly Price: " + hourly_price);
                choices.add(slope_no);
                slopeIndex++;
            }

            if (slopeIndex == 1) {
                System.out.println("No slopes available at this station.");
                return null;
            }

            System.out.print("Please Enter Your Option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (option < 1 || option >= slopeIndex) {
                System.out.println("Invalid option. Please choose again.\n");
                return null;
            }

            return choices.get(option - 1);

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        statement.close();
        return null;

    }

    private static Integer showTimeSlots(Connection con, String stationName, Integer slopeNo) throws SQLException{

        Scanner scanner = new Scanner(System.in);
        Statement statement = con.createStatement();

        try {

            String querySQL = "SELECT ts.timeslot_id, ts.date, ts.start_time, ts.end_time" +
                    " FROM " + openingsTable + " op, " + timeslotTable + " ts" +
                    " WHERE op.timeslot_id = ts.timeslot_id AND op.location_name = '" + stationName + "' AND op.slope_no = " + slopeNo;


            java.sql.ResultSet timeSlotResultSet = statement.executeQuery(querySQL);
            System.out.println("Select a Time Slot:");

            int timeSlotIndex = 1;
            List<Integer> choices = new ArrayList<>();
            while (timeSlotResultSet.next()) {
                Integer timeSlotId = timeSlotResultSet.getInt(1);
                String date = timeSlotResultSet.getString(2);
                String start_time = timeSlotResultSet.getString(3);
                String end_time = timeSlotResultSet.getString(4);
                System.out.println(timeSlotIndex + ". " + date + " - " + start_time + " to " + end_time);
                choices.add(timeSlotId);
                timeSlotIndex++;
            }

            if (timeSlotIndex == 1) {
                System.out.println("No time slots available at this station.");
                return null;
            }

            System.out.print("Please Enter Your Option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (option < 1 || option >= timeSlotIndex) {
                System.out.println("Invalid option. Please choose again.\n");
                return null;
            }

            return choices.get(option - 1);

        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }
        statement.close();

        return null;
    }

    private static void insertReservation(Connection con, String skier_email, String location_name, Integer slope_no, Integer timeslot_id) throws SQLException{

        System.out.println("Booking reservation...");

        int reservation_id = generateUniqueReservationId(con);

        //Insert reservation with only these attributes
        String insertSQL = "INSERT INTO " + reservationTable + " (reservation_id, skier_email, location_name, slope_no, timeslot_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
        preparedStatement.setInt(1, reservation_id);
        preparedStatement.setString(2, skier_email);
        preparedStatement.setString(3, location_name);
        preparedStatement.setInt(4, slope_no);
        preparedStatement.setInt(5, timeslot_id);

        try {
            preparedStatement.executeUpdate();
            System.out.println("Reservation booked successfully!");
            System.out.println("Reservation ID: " + reservation_id);
        } catch (SQLException e) {
            sqlCode = e.getErrorCode(); // Get SQLCODE
            sqlState = e.getSQLState(); // Get SQLSTATE

            // Your code to handle errors comes here;
            // something more meaningful than a print would be good
            System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
            System.out.println(e);
        }

        preparedStatement.close();

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
