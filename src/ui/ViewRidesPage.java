package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import model.Ride;
import storage.RideStorage;

public class ViewRidesPage extends JFrame {
    private DefaultListModel<String> listModel;
    private JList<String> rideList;
    private ArrayList<Ride> rides; // track Ride objects for DB operations

    public ViewRidesPage() {
        Theme.apply();

        rides = RideStorage.loadRides(); // load rides from DB
        listModel = new DefaultListModel<>();
        for (Ride r : rides) listModel.addElement(r.toString());

        setTitle("View Rides");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 420);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout(10,10));

        // Header
        JLabel header = new JLabel("Available Rides");
        header.setForeground(Theme.FORE);
        header.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        header.setFont(header.getFont().deriveFont(18f));
        add(header, BorderLayout.NORTH);

        // Ride list
        rideList = new JList<>(listModel);
        rideList.setBackground(new Color(60,60,60));
        rideList.setForeground(Theme.FORE);
        add(new JScrollPane(rideList), BorderLayout.CENTER);

        // Buttons panel
        JPanel btns = new JPanel();
        btns.setBackground(Theme.BG);

        // Left group (primary actions)
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        left.setBackground(Theme.BG);
        JButton add = smallButton("Add Ride");                // NEW: add via view page
        JButton edit = styledButton("Edit Selected");
        JButton delete = styledButton("Delete Selected");
        left.add(add); left.add(edit); left.add(delete);

        // Right group (utility)
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        right.setBackground(Theme.BG);
        JButton refresh = smallButton("Refresh");
        JButton back = smallButton("Back");
        right.add(refresh); right.add(back);

        // Combine groups
        btns.setLayout(new BorderLayout());
        btns.add(left, BorderLayout.WEST);
        btns.add(right, BorderLayout.EAST);
        add(btns, BorderLayout.SOUTH);

        // Button actions
        add.addActionListener(e -> new AddRidePage(this)); // open Add page with this as parent (ensures reload)
        edit.addActionListener(e -> editSelected());
        delete.addActionListener(e -> deleteSelected());
        refresh.addActionListener(e -> reloadRides());
        back.addActionListener(e -> { dispose(); new HomePage(); });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // smaller, subtler button used for less prominent actions
    private JButton smallButton(String text){
        JButton b = new JButton(text);
        b.setBackground(Theme.PANEL);
        b.setForeground(Theme.FORE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,10,6,10));
        return b;
    }

    private JButton styledButton(String text){
        JButton b = new JButton(text);
        b.setBackground(Theme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    // Reload rides from MySQL (public so EditRidePage can call it)
    public void reloadRides(){
        rides = RideStorage.loadRides();
        listModel.clear();
        for (Ride r : rides) listModel.addElement(r.toString());
    }

    // Delete selected ride
    private void deleteSelected(){
        int idx = rideList.getSelectedIndex();
        if (idx >= 0){
            Ride rideToDelete = rides.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this ride?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION){
                RideStorage.deleteRide(rideToDelete.getId());
                reloadRides();
                JOptionPane.showMessageDialog(this, "Ride deleted!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a ride", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Edit selected ride
    private void editSelected(){
        int idx = rideList.getSelectedIndex();
        if (idx >= 0){
            Ride rideToEdit = rides.get(idx);
            new EditRidePage(rideToEdit, this); // opens edit window, passes current page for refresh
        } else {
            JOptionPane.showMessageDialog(this, "Select a ride to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
