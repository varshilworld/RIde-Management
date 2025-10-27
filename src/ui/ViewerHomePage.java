package ui;

import model.Ride;
import storage.RideStorage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ViewerHomePage extends JFrame {
    private String userEmail;
    private JTextField srcField;
    private JTextField dstField;
    private DefaultListModel<Ride> listModel;
    private JList<Ride> resultsList;
    private java.util.List<Ride> currentResults = new ArrayList<>();

    public ViewerHomePage(String userEmail) {
        this.userEmail = userEmail;
        Theme.apply();
        setTitle("Viewer - Search Rides");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        JPanel top = new JPanel(new GridBagLayout());
        top.setBackground(Theme.PANEL);
        top.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        top.add(label("Source:"), c);
        c.gridx = 1;
        srcField = new JTextField(18);
        top.add(srcField, c);

        c.gridx = 0; c.gridy = 1;
        top.add(label("Destination:"), c);
        c.gridx = 1;
        dstField = new JTextField(18);
        top.add(dstField, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        JButton searchBtn = styledButton("Search");
        top.add(searchBtn, c);

        searchBtn.addActionListener(e -> doSearch());

        listModel = new DefaultListModel<>();
        resultsList = new JList<>(listModel);
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ride) setText(((Ride) value).toString());
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(resultsList);

    JPanel bottom = new JPanel();
        bottom.setBackground(Theme.BG);
        JButton bookBtn = styledButton("Book Selected Ride");
    JButton profileBtn = styledButton("My Profile");
    JButton logoutBtn = styledButton("Logout");
    bottom.add(bookBtn);
    bottom.add(profileBtn);
    bottom.add(logoutBtn);

        bookBtn.addActionListener(e -> handleBook());
        logoutBtn.addActionListener(e -> {
            dispose();
            new ViewerLoginPage();
        });

        profileBtn.addActionListener(e -> {
            // open profile page
            new ViewerProfilePage(userEmail);
        });

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
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

    private void doSearch() {
        String src = srcField.getText().trim();
        String dst = dstField.getText().trim();
        currentResults = RideStorage.searchRidesBySourceDestination(src, dst);
        listModel.clear();
        for (Ride r : currentResults) listModel.addElement(r);
        if (currentResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No rides found for given source/destination.", "No results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleBook() {
        Ride sel = resultsList.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Please select a ride to book.", "Select", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // ask how many seats to book (1..available)
        int max = sel.getSeatsAvailable();
        String seatsStr = (String) JOptionPane.showInputDialog(this,
                "Enter number of seats to book (1 - " + max + "):",
                "Seats to book",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "1");
        if (seatsStr == null) return; // cancelled
        int seatsToBook;
        try {
            seatsToBook = Integer.parseInt(seatsStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (seatsToBook <= 0 || seatsToBook > max) {
            JOptionPane.showMessageDialog(this, "Invalid seat count.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Book " + seatsToBook + " seat(s) for:\n" + sel.toString() + "\nProceed?", "Confirm Booking", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = RideStorage.bookRide(sel.getId(), userEmail, seatsToBook);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Booking confirmed! A confirmation has been saved to the database.", "Booked", JOptionPane.INFORMATION_MESSAGE);
            // refresh results: either update seats or remove if gone
            doSearch();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to book ride. It may have been taken by someone else or invalid seats.", "Error", JOptionPane.ERROR_MESSAGE);
            // refresh to get latest state
            doSearch();
        }
    }
}
