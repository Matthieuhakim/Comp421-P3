import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Date;

public class AddSkier {

  private static int sqlCode = 0;      // Variable to hold SQLCODE
  private static String sqlState = "00000";  // Variable to hold SQLSTATE

  private static String skierTable = "Skier";
  private static String userTable = "User";

  public static void execute(Connection con) throws SQLException {

    //Ask for skier email
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter your email: ");
    String skier_email = scanner.nextLine();

    //Check if skier exists
    if (checkSkierExists(con, skier_email)) {
      System.out.println(
          "User with email " + skier_email + " already exists. Please use a different email.");
      return;
    }

    System.out.println("Enter your full name: ");
    String skierName = scanner.nextLine();

    System.out.println("Enter your password: ");
    String skierPassword = scanner.nextLine();

    System.out.println("Enter your date of birth: (YYYY-MM-DD) ");
    String skierDOBString = scanner.nextLine();
    Date skierDOB;
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      java.util.Date parsed = format.parse(skierDOBString);
      skierDOB = new Date(parsed.getTime());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    System.out.println("Enter your experience level: (1 to 10) ");
    int skierExperience = scanner.nextInt();
    scanner.nextLine();

    System.out.println("Enter your address: ");
    String skierAddress = scanner.nextLine();

    System.out.println("Enter your credit card number for payments: ");
    String skierCreditCardNumber = scanner.nextLine();

    System.out.println("Enter your credit card expiry date: (YYYY-MM)");
    String skierCreditCardExpiryString = scanner.nextLine() + "-01";
    Date skierCreditCardExpiryDate;
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      java.util.Date parsed = format.parse(skierCreditCardExpiryString);
      skierCreditCardExpiryDate = new Date(parsed.getTime());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

//    scanner.close();

    // Insert User
    insertUser(con, skier_email, skierName, skierPassword, skierDOB);
    //Insert Skier
    insertSkier(con, skier_email, skierExperience, skierAddress, skierCreditCardNumber, skierCreditCardExpiryDate);

  }

  private static boolean checkSkierExists(Connection con, String skier_email) throws SQLException{
    Statement statement = con.createStatement();
    try {
      String querySQL = "SELECT * from " + userTable + " WHERE email = '" + skier_email + "'";
      java.sql.ResultSet rs = statement.executeQuery(querySQL);
      if (!rs.next()) {
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

  private static void insertSkier(Connection con, String skier_email, int skierExperience, String skierAddress, String skierCreditCardNumber, Date skierCreditCardExpiry) throws SQLException{

    System.out.println("Creating skier profile...");

    //Insert reservation with only these attributes
    String insertSQL = "INSERT INTO " + skierTable + " (email, experience_level, address, credit_card_num, credit_card_exp) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
    preparedStatement.setString(1, skier_email);
    preparedStatement.setInt(2, skierExperience);
    preparedStatement.setString(3, skierAddress);
    preparedStatement.setString(4, skierCreditCardNumber);
    preparedStatement.setDate(5, skierCreditCardExpiry);

    try {
      preparedStatement.executeUpdate();
      System.out.println("Skier profile created successfully!");
      System.out.println("Skier email: " + skier_email);
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

  private static void insertUser(Connection con, String skier_email, String name, String password, Date dateOfBirth) throws SQLException{

    System.out.println("Creating user profile...");

    //Insert reservation with only these attributes
    String insertSQL = "INSERT INTO " + userTable + " (email, name, password, birth_date) VALUES (?, ?, ?, ?)";
    PreparedStatement preparedStatement = con.prepareStatement(insertSQL);
    preparedStatement.setString(1, skier_email);
    preparedStatement.setString(2, name);
    preparedStatement.setString(3, password);
    preparedStatement.setDate(4, dateOfBirth);

    try {
      preparedStatement.executeUpdate();
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

}
