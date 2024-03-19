import java.sql.*;
import java.sql.Date;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AddSkier extends JFrame {

  private static final String skierTable = "Skier";
  private static final String userTable = "User";
  private final Connection con;
  private final JTextField emailField;
    private final JTextField nameField;
    private final JTextField passwordField;
    private final JTextField dobField;
    private final JTextField experienceField;
    private final JTextField addressField;
    private final JTextField creditCardNumberField;
    private final JTextField creditCardExpiryField;
  private final JButton submitButton;

  public AddSkier(Connection con) {
    this.con = con;
    setTitle("Register Skier");
    setSize(300, 400);
    setLayout(new GridLayout(9, 2));

    add(new JLabel("Email:"));
    emailField = new JTextField();
    add(emailField);

    add(new JLabel("Full Name:"));
    nameField = new JTextField();
    add(nameField);

    add(new JLabel("Password:"));
    passwordField = new JTextField();
    add(passwordField);

    add(new JLabel("Date of Birth (YYYY-MM-DD):"));
    dobField = new JTextField();
    add(dobField);

    add(new JLabel("Experience Level (1 to 10):"));
    experienceField = new JTextField();
    add(experienceField);

    add(new JLabel("Address:"));
    addressField = new JTextField();
    add(addressField);

    add(new JLabel("Credit Card Number:"));
    creditCardNumberField = new JTextField();
    add(creditCardNumberField);

    add(new JLabel("Credit Card Expiry (YYYY-MM):"));
    creditCardExpiryField = new JTextField();
    add(creditCardExpiryField);

    submitButton = new JButton("Submit");
    add(submitButton);
    submitButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        submitSkier();
      }
    });

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setVisible(true);
  }

  private void submitSkier() {
    String skierEmail = emailField.getText();
    String skierName = nameField.getText();
    String skierPassword = passwordField.getText();
    String dobString = dobField.getText();
    String experienceString = experienceField.getText();
    String skierAddress = addressField.getText();
    String creditCardNumber = creditCardNumberField.getText();
    String creditCardExpiry = creditCardExpiryField.getText() + "-01";

    if (!checkSkierExists(skierEmail)) {
      try {
        java.sql.Date skierDOB = java.sql.Date.valueOf(dobString);
        java.sql.Date skierCreditCardExpiry = java.sql.Date.valueOf(creditCardExpiry);
        int skierExperience = Integer.parseInt(experienceString);

        // Insert User and Skier
        insertUser(skierEmail, skierName, skierPassword, skierDOB);
        insertSkier(skierEmail, skierExperience, skierAddress, creditCardNumber, skierCreditCardExpiry);
        JOptionPane.showMessageDialog(this, "Skier profile created successfully!");
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid experience level.", "Error", JOptionPane.ERROR_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid or missing field", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else {
      JOptionPane.showMessageDialog(this, "User with email " + skierEmail + " already exists. Please use a different email.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private boolean checkSkierExists(String skier_email) {
    try (Statement statement = con.createStatement()) {
      String querySQL = "SELECT * FROM " + userTable + " WHERE email = '" + skier_email + "'";
      ResultSet rs = statement.executeQuery(querySQL);
      if (rs.next()) {
        rs.close();
        return true;
      }
    } catch (SQLException e) {
      showErrorDialog("Error checking if skier exists: " + e.getMessage());
    }
    return false;
  }

  private void insertSkier(String skier_email, int skierExperience, String skierAddress, String skierCreditCardNumber, Date skierCreditCardExpiry) {
    String insertSQL = "INSERT INTO " + skierTable + " (email, experience_level, address, credit_card_num, credit_card_exp) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = con.prepareStatement(insertSQL)) {
      preparedStatement.setString(1, skier_email);
      preparedStatement.setInt(2, skierExperience);
      preparedStatement.setString(3, skierAddress);
      preparedStatement.setString(4, skierCreditCardNumber);
      preparedStatement.setDate(5, new java.sql.Date(skierCreditCardExpiry.getTime()));
      preparedStatement.executeUpdate();
      //JOptionPane.showMessageDialog(this, "Skier profile created successfully!");
    } catch (SQLException e) {
      showErrorDialog("Error creating skier profile: " + e.getMessage());
    }
  }

  private void insertUser(String skier_email, String name, String password, Date dateOfBirth) {
    String insertSQL = "INSERT INTO " + userTable + " (email, name, password, birth_date) VALUES (?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = con.prepareStatement(insertSQL)) {
      preparedStatement.setString(1, skier_email);
      preparedStatement.setString(2, name);
      preparedStatement.setString(3, password);
      preparedStatement.setDate(4, new java.sql.Date(dateOfBirth.getTime()));
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      showErrorDialog("Error creating user profile: " + e.getMessage());
    }
  }

  private void showErrorDialog(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  // Additional methods and class closing braces
}
