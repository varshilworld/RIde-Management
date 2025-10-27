package ui;

import javax.swing.*;
import java.awt.*;

public class ViewerProfilePage extends JFrame {
    private String userEmail;

    public ViewerProfilePage(String userEmail) {
        this.userEmail = userEmail;
        Theme.apply();
        setTitle("My Profile");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 200);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.PANEL);
        center.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        center.add(label("Email:"), c);
        c.gridx = 1;
        center.add(new JLabel(userEmail), c);

        JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG);
        JButton viewRidesBtn = styledButton("View My Rides");
        JButton closeBtn = styledButton("Close");
        bottom.add(viewRidesBtn);
        bottom.add(closeBtn);

        viewRidesBtn.addActionListener(e -> new MyBookingsPage(userEmail));
        closeBtn.addActionListener(e -> dispose());

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
}
