package screens;

import utils.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {
    public Login() {
        setTitle("Login - Banking App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);

        setIconImage(new ImageIcon(getClass().getResource("/img/g_icon.jpg")).getImage());

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 250));

        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JTextField emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setBorder(BorderFactory.createTitledBorder("Email"));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));

        mainPanel.add(emailField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        registerButton.setBackground(new Color(230, 230, 250));
        registerButton.setForeground(new Color(0, 120, 215));
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(registerButton);

        add(mainPanel);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DBConnection.connect()) {
                String query = "SELECT id, name, password FROM users WHERE email = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword.equals(hashPassword(password))) {
                        int userId = rs.getInt("id");
                        String name = rs.getString("name");
                        JOptionPane.showMessageDialog(this, "Welcome back, " + name + "!");
                        dispose();
                        new Dashboard(userId);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid email or password!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        registerButton.addActionListener(e -> {
            dispose();
            new Register();
        });

        passwordField.addActionListener(e -> loginButton.doClick());

        setVisible(true);
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}