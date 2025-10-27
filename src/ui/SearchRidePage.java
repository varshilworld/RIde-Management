package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import model.Ride;
import storage.RideStorage;

public class SearchRidePage extends JFrame {
    private JTextField queryField;
    private DefaultListModel<String> listModel;
    private JList<String> resultList;
    private ArrayList<Ride> results;

    public SearchRidePage() {
        Theme.apply();
        setTitle("Search Rides");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout(10,10));

        // Top: query bar
        JPanel top = new JPanel(new BorderLayout(8,8));
        top.setBackground(Theme.BG);
        top.setBorder(BorderFactory.createEmptyBorder(8,8,0,8));

        queryField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        searchBtn.setBackground(Theme.ACCENT);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        clearBtn.setBackground(Theme.PANEL);
        clearBtn.setForeground(Theme.FORE);
        clearBtn.setFocusPainted(false);

        top.add(queryField, BorderLayout.CENTER);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightTop.setBackground(Theme.BG);
        rightTop.add(searchBtn);
        rightTop.add(clearBtn);
        top.add(rightTop, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Center: results list
        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setBackground(new Color(60,60,60));
        resultList.setForeground(Theme.FORE);
        add(new JScrollPane(resultList), BorderLayout.CENTER);

        // Bottom: action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bottom.setBackground(Theme.BG);
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton backBtn = new JButton("Back");

        editBtn.setBackground(Theme.ACCENT);
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);

        deleteBtn.setBackground(Theme.WARN);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);

        backBtn.setBackground(Theme.PANEL);
        backBtn.setForeground(Theme.FORE);
        backBtn.setFocusPainted(false);

        bottom.add(editBtn);
        bottom.add(deleteBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        searchBtn.addActionListener(e -> doSearch());
        clearBtn.addActionListener(e -> {
            queryField.setText("");
            listModel.clear();
        });

        backBtn.addActionListener(e -> {
            dispose();
            new HomePage();
        });

        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        // Double-click to edit
        resultList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelected();
                }
            }
        });

        // Press Enter to search
        queryField.addActionListener(e -> doSearch());

        // Initially show all rides
        loadAllResults();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void doSearch() {
        String q = queryField.getText().trim();
        results = RideStorage.searchRides(q);
        refreshListModel();
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No rides found for: " + q, "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadAllResults() {
        results = RideStorage.loadRides();
        refreshListModel();
    }

    private void refreshListModel() {
        listModel.clear();
        if (results != null) {
            for (Ride r : results) {
                listModel.addElement(String.format("[%d] %s → %s | ₹%.2f", r.getId(), r.getSource(), r.getDestination(), r.getFare()));
            }
        }
    }

    private void editSelected() {
        int idx = resultList.getSelectedIndex();
        if (idx >= 0) {
            Ride r = results.get(idx);
            new EditRidePage(r, null); // pass null parent — editing here won't auto-refresh this list, so we reload after edit
            // Wait briefly and reload (safer to reload after Edit window closes)
            // We'll start a small thread to poll for window close; simpler: show dialog asking user to refresh
            int resp = JOptionPane.showConfirmDialog(this, "After editing, click Refresh to see changes. Refresh now?", "Refresh", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) doSearch();
        } else {
            JOptionPane.showMessageDialog(this, "Select a ride to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelected() {
        int idx = resultList.getSelectedIndex();
        if (idx >= 0) {
            Ride r = results.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected ride?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                RideStorage.deleteRide(r.getId());
                JOptionPane.showMessageDialog(this, "Ride deleted.");
                doSearch(); // refresh results
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a ride to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
