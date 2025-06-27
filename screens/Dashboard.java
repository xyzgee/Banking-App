package screens;

import utils.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {
    private int userId;
    private JLabel balanceLabel;
    private JTextField amountField;

    public Dashboard(int userId) {
        this.userId = userId;
        setTitle("Banking App Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Set custom icon
        setIconImage(new ImageIcon(getClass().getResource("/img/g_icon.jpg")).getImage());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(245, 245, 250));

        JLabel label = new JLabel("Welcome to Your Dashboard!");
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(label);

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(balanceLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.X_AXIS));
        amountPanel.setOpaque(false);

        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        amountField.setMaximumSize(new Dimension(120, 40));
        amountField.setBorder(BorderFactory.createTitledBorder("Amount"));

        amountPanel.add(amountField);

        mainPanel.add(amountPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton logoutButton = new JButton("Logout");

        depositButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        withdrawButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        depositButton.setBackground(new Color(0, 180, 90));
        depositButton.setForeground(Color.WHITE);
        withdrawButton.setBackground(new Color(220, 53, 69));
        withdrawButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(230, 230, 250));
        logoutButton.setForeground(new Color(0, 120, 215));

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel);

        // Load initial balance
        refreshBalance();

        // Button actions
        depositButton.addActionListener(e -> performTransaction(true));
        withdrawButton.addActionListener(e -> performTransaction(false));
        logoutButton.addActionListener(e -> {
            dispose();
            new Login();
        });

        add(mainPanel);
        setVisible(true);
    }

    private void refreshBalance() {
        try (Connection conn = DBConnection.connect()) {
            String query = "SELECT balance FROM accounts WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceLabel.setText("Balance: ₹" + balance);
            }
        } catch (Exception ex) {
            balanceLabel.setText("Balance: Error");
        }
    }

    private void performTransaction(boolean isDeposit) {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive number.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            // Get current balance
            String selectQuery = "SELECT balance FROM accounts WHERE user_id = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                double newBalance = isDeposit ? currentBalance + amount : currentBalance - amount;
                if (!isDeposit && newBalance < 0) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                    return;
                }
                // Update balance
                String updateQuery = "UPDATE accounts SET balance = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setDouble(1, newBalance);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
                refreshBalance();
                amountField.setText("");
                JOptionPane.showMessageDialog(this, (isDeposit ? "Deposit" : "Withdrawal") + " successful!");
                recordTransaction(userId, amount, isDeposit ? "deposit" : "withdraw");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
        }
    }

    private void recordTransaction(int accountId, double amount, String type) {
        try (Connection conn = DBConnection.connect()) {
            String query = "INSERT INTO transactions(account_id, amount, type, date) VALUES (?, ?, ?, datetime('now'))";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            stmt.setDouble(2, amount);
            stmt.setString(3, type);
            stmt.executeUpdate();
        } catch (Exception ex) {
            // Handle error
        }
    }
}
