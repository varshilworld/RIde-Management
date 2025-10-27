package ui;

import storage.UserStorage;

import javax.swing.*;
import java.awt.*;

public class ViewerLoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public ViewerLoginPage() {
        Theme.apply();
        setTitle("Viewer Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 240);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.PANEL);
        center.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        center.add(label("Email:"), c);
        c.gridx = 1;
        emailField = new JTextField(22);
        center.add(emailField, c);

        c.gridx = 0; c.gridy = 1;
        center.add(label("Password:"), c);
        c.gridx = 1;
        passwordField = new JPasswordField(22);
        center.add(passwordField, c);

        JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG);
        JButton loginBtn = styledButton("Login");
        JButton registerBtn = styledButton("Register");
        JButton backBtn = styledButton("Back");
        bottom.add(loginBtn);
        bottom.add(registerBtn);
        bottom.add(backBtn);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.FORE);
        return l;
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(Theme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        return b;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password", "Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (UserStorage.authenticateUser(email, pass)) {
            dispose();
            new ViewerHomePage(email);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials. If you are a new user, click Register.", "Login failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleRegister() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password", "Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = UserStorage.registerUser(email, pass);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Registration successful. You are now logged in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new ViewerHomePage(email);
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed (user may already exist).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
