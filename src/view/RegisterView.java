package view;

import controller.UserController;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private JTextField tfUsername, tfEmail, tfFullname;
    private JPasswordField pfPassword;
    private JButton btnRegister, btnToLogin;

    public RegisterView() {
        setTitle("Register");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridLayout(4,2,8,8));
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        center.add(new JLabel("Username:"));
        tfUsername = new JTextField(); center.add(tfUsername);
        center.add(new JLabel("Password:"));
        pfPassword = new JPasswordField(); center.add(pfPassword);
        center.add(new JLabel("Email:"));
        tfEmail = new JTextField(); center.add(tfEmail);
        center.add(new JLabel("Full name:"));
        tfFullname = new JTextField(); center.add(tfFullname);

        JPanel south = new JPanel();
        btnRegister = new JButton("Register");
        btnToLogin = new JButton("Go to Login");
        south.add(btnRegister);
        south.add(btnToLogin);

        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        btnRegister.addActionListener(e -> handleRegister());
        btnToLogin.addActionListener(e -> {
            this.dispose();
            new LoginView();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleRegister() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());
        String email = tfEmail.getText().trim();
        String fullname = tfFullname.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        boolean ok = UserController.register(username, password, email, fullname);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            // đi tới login
            this.dispose();
            new LoginView();
        } else {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại (có thể user đã tồn tại).");
        }
    }
}
