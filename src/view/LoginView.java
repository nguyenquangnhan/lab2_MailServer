package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin, btnToRegister;

    public LoginView() {
        setTitle("Login");
        setSize(320,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridLayout(2,2,8,8));
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        center.add(new JLabel("Username:"));
        tfUsername = new JTextField(); center.add(tfUsername);
        center.add(new JLabel("Password:"));
        pfPassword = new JPasswordField(); center.add(pfPassword);

        JPanel south = new JPanel();
        btnLogin = new JButton("Login");
        btnToRegister = new JButton("Register");
        south.add(btnLogin);
        south.add(btnToRegister);

        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> {
            String username = tfUsername.getText().trim();
            String password = new String(pfPassword.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập username và password.");
                return;
            }
            User u = UserController.login(username, password);
            if (u != null) {
                JOptionPane.showMessageDialog(this, "Login thành công. Xin chào " + u.getFullname());
                this.dispose();
                new MailView(u);
            } else {
                JOptionPane.showMessageDialog(this, "Login thất bại.");
            }
        });

        btnToRegister.addActionListener(e -> {
            this.dispose();
            new RegisterView();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
