package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbUtil {
    private static final String DB_URL = "jdbc:h2:./data/bankdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String schema = Files.readString(Paths.get("src/main/resources/sql/schema.sql"));
            stmt.execute(schema);
            // ensure employer columns exist for older DB files (schema migrations)
            try {
                stmt.execute("ALTER TABLE accounts ADD COLUMN employer_name VARCHAR(200)");
            } catch (Exception ignored) {}
            try {
                stmt.execute("ALTER TABLE accounts ADD COLUMN employer_address VARCHAR(255)");
            } catch (Exception ignored) {}
            // seed default admin if not present
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS c FROM users WHERE username = ?")) {
                ps.setString(1, "admin");
                try (ResultSet rs = ps.executeQuery()) {
                    boolean need = true;
                    if (rs.next()) {
                        need = rs.getInt("c") == 0;
                    }
                    if (need) {
                        try (PreparedStatement ins = conn.prepareStatement("INSERT INTO users(full_name,address,marital_status,username,email,password_hash,role) VALUES(?,?,?,?,?,?,?)")) {
                            ins.setString(1, "Administrator");
                            ins.setString(2, "Head Office");
                            ins.setString(3, "N/A");
                            ins.setString(4, "admin");
                            ins.setString(5, "admin@example.com");
                            ins.setString(6, HashUtil.sha256("admin"));
                            ins.setString(7, "ADMIN");
                            ins.executeUpdate();
                            System.out.println("[db] Seeded default admin user: username=admin password=admin");
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
