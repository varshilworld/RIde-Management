package ui;

import model.Booking;
import storage.RideStorage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MyBookingsPage extends JFrame {
    private String userEmail;

    public MyBookingsPage(String userEmail) {
        this.userEmail = userEmail;
        Theme.apply();
        setTitle("My Bookings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Bookings for: " + userEmail);
        header.setForeground(Theme.FORE);
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        DefaultListModel<Booking> model = new DefaultListModel<>();
        JList<Booking> list = new JList<>(model);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Booking) setText(((Booking) value).toString());
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(list);

        JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG);
        JButton refreshBtn = styledButton("Refresh");
        JButton closeBtn = styledButton("Close");
        bottom.add(refreshBtn);
        bottom.add(closeBtn);

        refreshBtn.addActionListener(e -> loadBookings(model));
        closeBtn.addActionListener(e -> dispose());

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadBookings(model);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadBookings(DefaultListModel<Booking> model) {
        ArrayList<Booking> bookings = RideStorage.getBookingsForUser(userEmail);
        model.clear();
        for (Booking b : bookings) model.addElement(b);
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
