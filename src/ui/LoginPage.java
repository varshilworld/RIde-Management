package ui;

import javax.swing.*;
import java.awt.*;
import ui.ViewerLoginPage;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        Theme.apply();
        setTitle("Ride Management - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.PANEL);
        center.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        center.add(label("Username:"), c);
        c.gridx = 1;
        usernameField = new JTextField(18);
        center.add(usernameField, c);

        c.gridx = 0; c.gridy = 1;
        center.add(label("Password:"), c);
        c.gridx = 1;
        passwordField = new JPasswordField(18);
        center.add(passwordField, c);

        JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG);
        JButton loginBtn = styledButton("Login");
        JButton exitBtn = styledButton("Exit");
    JButton viewerBtn = styledButton("Viewer Login");
        bottom.add(loginBtn);
    bottom.add(viewerBtn);
        bottom.add(exitBtn);

        loginBtn.addActionListener(e -> handleLogin());
        viewerBtn.addActionListener(e -> {
            dispose();
            new ViewerLoginPage();
        });
        exitBtn.addActionListener(e -> System.exit(0));

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
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.equals("admin") && pass.equals("1234")) {
            dispose();
            new HomePage();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login failed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
