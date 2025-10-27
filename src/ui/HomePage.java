package ui;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    private ViewRidesPage currentViewPage; // track currently open ViewRidesPage

    public HomePage() {
        Theme.apply();
        setTitle("Ride Management - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 320);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout(12,12));

        JLabel header = new JLabel("Ride Management");
        header.setForeground(Theme.FORE);
        header.setFont(header.getFont().deriveFont(20f));
        header.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2,2,12,12));
        center.setBackground(Theme.BG);
        center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton add = createCard("Add Ride");
        JButton view = createCard("View Rides");
        JButton search = createCard("Search Rides");
        JButton logout = createCard("Logout");

        // Open AddRidePage, pass current ViewRidesPage reference
        add.addActionListener(e -> {
            dispose();
            new AddRidePage(currentViewPage);
        });

        // Open ViewRidesPage and store reference
        view.addActionListener(e -> {
            dispose();
            currentViewPage = new ViewRidesPage();
        });

        // Open Search page
        search.addActionListener(e -> {
            dispose();
            new SearchRidePage(); // opens the search UI
        });

        logout.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        center.add(add);
        center.add(view);
        center.add(search);
        center.add(logout);
        add(center, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createCard(String title) {
        JButton b = new JButton(title);
        b.setBackground(Theme.PANEL);
        b.setForeground(Theme.FORE);
        b.setFont(b.getFont().deriveFont(16f));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT, 2),
            BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        return b;
    }
}
