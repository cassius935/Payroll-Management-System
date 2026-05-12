import src.DBConnection;
import java.sql.*;

public class DBTest {
    public static void main(String[] args) {
        System.out.println("Testing database connection and admin accounts...");

        try {
            Connection con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("❌ Database connection failed!");
                return;
            }

            System.out.println("✅ Database connection successful!");

            // Check admin table
            System.out.println("\n--- Checking admin table ---");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM admin");

            boolean hasAdmins = false;
            while (rs.next()) {
                hasAdmins = true;
                String adminLogin = rs.getString("admin_login");
                String password = rs.getString("password");
                System.out.println("Admin: " + adminLogin + " / " + password);
            }

            if (!hasAdmins) {
                System.out.println("❌ No admin accounts found in admin table!");
                System.out.println("Please run: mysql -u root < database_setup.sql");
            } else {
                System.out.println("✅ Admin accounts found!");
            }

            // Check useracc table
            System.out.println("\n--- Checking useracc table ---");
            rs = stmt.executeQuery("SELECT * FROM useracc");

            boolean hasWorkers = false;
            while (rs.next()) {
                hasWorkers = true;
                String userName = rs.getString("user_name");
                String password = rs.getString("password");
                System.out.println("Worker: " + userName + " / " + password);
            }

            if (!hasWorkers) {
                System.out.println("❌ No worker accounts found in useracc table!");
            } else {
                System.out.println("✅ Worker accounts found!");
            }

            // Test login queries
            System.out.println("\n--- Testing login queries ---");

            // Test admin login
            PreparedStatement pst = con.prepareStatement("SELECT * FROM admin WHERE admin_login = ? AND password = ?");
            pst.setString(1, "admin1");
            pst.setString(2, "12345678910");
            rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Admin1 login query works!");
            } else {
                System.out.println("❌ Admin1 login query failed!");
            }

            // Test worker login
            pst = con.prepareStatement("SELECT * FROM useracc WHERE user_name = ? AND password = ?");
            pst.setString(1, "worker1");
            pst.setString(2, "worker123");
            rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Worker1 login query works!");
            } else {
                System.out.println("❌ Worker1 login query failed!");
            }

            rs.close();
            stmt.close();
            con.close();

            System.out.println("\n--- Summary ---");
            System.out.println("If you see ❌ above, run: mysql -u root < database_setup.sql");
            System.out.println("If everything shows ✅, then login should work!");

        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}