package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class UserDAO {
    private static final String TXT_FILE = "users.txt";

    // Lưu user vào file txt
    public static void saveToFile(User u) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TXT_FILE, true))) {
            // format: username,password,email,fullname
            bw.write(u.getUsername() + "," + u.getPassword() + "," + u.getEmail() + "," + u.getFullname());
            bw.newLine();
        }
    }

    // Lưu user vào database
    public static boolean saveToDatabase(User u) {
        String sql = "INSERT INTO users(username,password,email,fullname) VALUES(?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getFullname());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            // ex.printStackTrace();
            return false;
        }
    }

    public static boolean existsByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static User authenticate(String username, String password) {
        String sql = "SELECT id,username,password,email,fullname FROM users WHERE username=? AND password=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("fullname")
                    );
                } else return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static User getByUsername(String username) {
        String sql = "SELECT id,username,password,email,fullname FROM users WHERE username=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("fullname")
                    );
                } else return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
