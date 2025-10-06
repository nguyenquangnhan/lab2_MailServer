package controller;

import model.User;
import model.UserDAO;

import java.io.IOException;

public class UserController {

    // đăng ký: lưu file + db
    public static boolean register(String username, String password, String email, String fullname) {
        try {
            if (UserDAO.existsByUsername(username)) {
                return false; // đã tồn tại
            }
            User u = new User(username, password, email, fullname);
            // lưu file (append)
            try {
                UserDAO.saveToFile(u);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                // vẫn cố lưu DB tiếp
            }
            // lưu DB
            boolean ok = UserDAO.saveToDatabase(u);
            return ok;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static User login(String username, String password) {
        return UserDAO.authenticate(username, password);
    }
}
