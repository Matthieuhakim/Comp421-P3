import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class simpleJDBC extends JFrame {
    private JButton lookupInstrcutorsJB;
    private JButton registerSkiersJB;
    private JButton bookReservationJB;
    private JButton addNewLessonPackJB;
    private JButton addEuiptoLessonPackJB;
    private JButton quitJB;

    private Connection con;

    public simpleJDBC() {
        // Initialize the DB connection when the application starts
        initConnection();

        // SetUp GUI components
        setupGUI();
    }

    private void initConnection() {
        String url = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/comp421";
        boolean connected = false;

        while (!connected) {
            // Prompt the user for user ID and password using input dialogs
            String your_userid = JOptionPane.showInputDialog(this, "Enter your user ID:", "Database Connection", JOptionPane.QUESTION_MESSAGE);
            // Check if the user clicked "Cancel"
            if (your_userid == null) {
                System.exit(0); // User chose to cancel, exit the application
            }

            String your_password = JOptionPane.showInputDialog(this, "Enter your password:", "Database Connection", JOptionPane.QUESTION_MESSAGE);
            // Check if the user clicked "Cancel" on the password prompt
            if (your_password == null) {
                System.exit(0); // User chose to cancel, exit the application
            }

            try {
                // Register JDBC driver and attempt to establish a connection
                DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
                con = DriverManager.getConnection(url, your_userid, your_password);
                connected = true; // Successful connection
            } catch (Exception e) {
                // Connection failed, show an error message and prompt again
                int choice = JOptionPane.showConfirmDialog(this, "Error connecting to database. Ensure that you entered the correct values, and that you are either using McGill wifi, or are routed through McGill wifi via a vpn. Try again?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                    System.exit(0); // User chose not to try again or closed the dialog, exit the application
                }
            }
        }

    }

    private void setupGUI() {
        //Window title
        setTitle("Ski Resort Management System");

        //Window size
        setSize(400, 300);

        //Layout
        setLayout(new FlowLayout());

        //Buttons init
        lookupInstrcutorsJB = new JButton("Look Up Instructors");
        registerSkiersJB = new JButton("Register a new Skier");
        bookReservationJB = new JButton("Book a Reservation");
        addNewLessonPackJB = new JButton("Add a New Lesson Package");
        addEuiptoLessonPackJB = new JButton("Add Equipment to a Lesson Package");
        quitJB = new JButton("Exit");

        // Add action listeners to buttons
        lookupInstrcutorsJB.addActionListener(e -> {

            lookup_instructors lookupGUI = new lookup_instructors(this.con);
            lookupGUI.setVisible(true);

        });

        registerSkiersJB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddSkier(con);
            }
        });

        bookReservationJB.addActionListener(e -> {

            book_reservation bookReservation = new book_reservation(con);
            bookReservation.setVisible(true);

        });

        addNewLessonPackJB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create and configure the dialog
                JDialog dialog = new JDialog((Frame)null, "Add New Lesson Package", true);
                dialog.setLayout(new FlowLayout());

                JTextField emailField = new JTextField(20);
                JTextField hourlyFeeField = new JTextField(20);
                JButton submitButton = new JButton("Submit");

                dialog.add(new JLabel("Instructor Email:"));
                dialog.add(emailField);
                dialog.add(new JLabel("Hourly Fee:"));
                dialog.add(hourlyFeeField);
                dialog.add(submitButton);

                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Retrieve user input
                        String email = emailField.getText();
                        double hourlyFee = Double.parseDouble(hourlyFeeField.getText());

                        // Validate inputs and add new lesson package to the database
                        boolean success = add_LessonPackage.addNewLessonPackage(con, email, hourlyFee);

                        // Provide feedback
                        JOptionPane.showMessageDialog(dialog, success ? "Lesson package added successfully." : "Failed to add lesson package.");

                        // Close the dialog
                        dialog.dispose();
                    }
                });

                // Display the dialog
                dialog.pack();
                dialog.setLocationRelativeTo(null); // Center on screen
                dialog.setVisible(true);
            }
        });

        addEuiptoLessonPackJB.addActionListener(e -> {

                AddEquipmentToLessonPackage.promptAddEquipmentToLessonPackage(con);
        });

        quitJB.addActionListener(e -> {

            try {
                con.close();
            } catch (SQLException ex) {
                System.exit(0);
                throw new RuntimeException(ex);
            }
            System.exit(0);

        });


        // Add buttons to the JFrame
        add(lookupInstrcutorsJB);
        add(registerSkiersJB);
        add(bookReservationJB);
        add(addNewLessonPackJB);
        add(addEuiptoLessonPackJB);
        add(quitJB);

        // Set the default close operation and make the window visible
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(simpleJDBC::new);
    }

}