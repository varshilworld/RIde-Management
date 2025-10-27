package ui;

import javax.swing.*;
import java.awt.*;

public class Theme {
    // Dark theme colors
    public static final Color BG = new Color(34,34,34);
    public static final Color PANEL = new Color(45,45,45);
    public static final Color FORE = new Color(220,220,220);
    public static final Color ACCENT = new Color(0,150,136); // teal
    public static final Color WARN = new Color(255,87,34); // orange

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Label.foreground", FORE);
        UIManager.put("Panel.background", BG);
        UIManager.put("Button.background", PANEL);
        UIManager.put("Button.foreground", FORE);
        UIManager.put("TextField.background", new Color(60,60,60));
        UIManager.put("TextField.foreground", FORE);
        UIManager.put("PasswordField.background", new Color(60,60,60));
        UIManager.put("PasswordField.foreground", FORE);
        UIManager.put("List.background", new Color(60,60,60));
        UIManager.put("List.foreground", FORE);
    }
}
