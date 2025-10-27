package ui;

import javax.swing.*;
import java.awt.*;
import model.Ride;
import storage.RideStorage;
import java.util.ArrayList;

public class AddRidePage extends JFrame {
    private JTextField srcField, destField, fareField;
    private ArrayList<Ride> rides = RideStorage.loadRides();
    private ViewRidesPage parentViewPage; // optional reference to refresh view

    // Constructor with optional ViewRidesPage reference
    public AddRidePage() { this(null); }

    public AddRidePage(ViewRidesPage parent) {
        this.parentViewPage = parent;

        Theme.apply();
        setTitle("Add Ride");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 280);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Theme.PANEL);
        form.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; form.add(label("Source:"), c);
        c.gridx=1; srcField = new JTextField(18); form.add(srcField, c);

        c.gridx=0; c.gridy=1; form.add(label("Destination:"), c);
        c.gridx=1; destField = new JTextField(18); form.add(destField, c);

        c.gridx=0; c.gridy=2; form.add(label("Fare (₹):"), c);
        c.gridx=1; fareField = new JTextField(18); form.add(fareField, c);

        // Buttons panel
        JPanel buttons = new JPanel();
        buttons.setBackground(Theme.BG);
        JButton addBtn = styledButton("Add Ride");
        JButton backBtn = styledButton("Back");
        buttons.add(addBtn); buttons.add(backBtn);

        addBtn.addActionListener(e -> addRide());
        backBtn.addActionListener(e -> { dispose(); new HomePage(); });

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel label(String t){
        JLabel l = new JLabel(t);
        l.setForeground(Theme.FORE);
        return l;
    }

    private JButton styledButton(String text){
        JButton b = new JButton(text);
        b.setBackground(Theme.ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private void addRide(){
        String s = srcField.getText().trim();
        String d = destField.getText().trim();
        String f = fareField.getText().trim();

        if (s.isEmpty() || d.isEmpty() || f.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double fare = Double.parseDouble(f);
            Ride newRide = new Ride(s, d, fare);

            // Save to DB
            RideStorage.saveRide(newRide);

            // Refresh ViewRidesPage if open
            if (parentViewPage != null) parentViewPage.reloadRides();

            JOptionPane.showMessageDialog(this, "Ride added!");
            srcField.setText(""); destField.setText(""); fareField.setText("");

        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Invalid fare", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
