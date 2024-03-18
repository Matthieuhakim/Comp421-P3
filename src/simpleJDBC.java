import java.sql.*;
import java.util.*;


class simpleJDBC {
    public static void main(String[] args) throws SQLException {


        // Register the driver.  You must register the driver before you can use it.
        try {
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }

        // This is the url you must use for DB2.
        //Note: This url may not valid now ! Check for the correct year and semester and server name.
        String url = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/comp421";

        //REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = null;
        String your_password = null;
        //AS AN ALTERNATIVE, you can just set your password in the shell environment in the Unix (as shown below) and read it from there.
        //$  export SOCSPASSWD=yoursocspasswd 
        if (your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        if (your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null) {
            System.err.println("Error!! do not have a password to connect to the database!");
            System.exit(1);
        }
        Connection con = DriverManager.getConnection(url, your_userid, your_password);

        Scanner scanner = new Scanner(System.in);
        boolean exited = false;
        while (!exited) {
            System.out.println("Ski Resort Main Menu");
            System.out.println("1. Look Up Instructors in Station");
            System.out.println("2. Register a new Skier");
            System.out.println("3. Book a Reservation");
            System.out.println("4. Add a new Lesson Package");
            System.out.println("5. Add Equipment to Lesson Package");
            System.out.println("6. Quit");
            System.out.print("Please Enter Your Option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    lookup_instructors.lookupInstructorsBySkiStation(con);
                    break;
                case 2:
                    AddSkier.execute(con);
                    break;
                case 3:
                    book_reservation.execute(con);
                    break;
                case 4:
                    add_LessonPackage.addNewLessonPackage(con);
                    break;
                case 5:
                    AddEquipmentToLessonPackage.promptAddEquipmentToLessonPackage(con);
		            break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    exited = true;
                    // Finally but importantly close the connection
                    con.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option. Please choose again.\n");
            }
        }

        // Finally but importantly close the connection
        con.close();
    }
}
