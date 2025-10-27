package app;

import ui.LoginPage;

public class Main {
    public static void main(String[] args) {
        // Launch login
        javax.swing.SwingUtilities.invokeLater(() -> new LoginPage());
    }
}
