package com.simulator;

import java.util.prefs.Preferences;

/**
 * Manages user preferences persistence using Java Preferences API.
 * Saves settings like theme, animation speed, etc. across sessions.
 */
public class PreferencesManager {

    private static final Preferences prefs = Preferences.userNodeForPackage(PreferencesManager.class);

    // Preference keys
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_ANIMATION_SPEED = "animationSpeed";
    private static final String KEY_SHOW_TOOLTIPS = "showTooltips";
    private static final String KEY_WINDOW_MAXIMIZED = "windowMaximized";
    private static final String KEY_LAST_MODULE = "lastModule";

    // Default values
    private static final boolean DEFAULT_DARK_MODE = false;
    private static final double DEFAULT_ANIMATION_SPEED = 1.0;
    private static final boolean DEFAULT_SHOW_TOOLTIPS = true;
    private static final boolean DEFAULT_WINDOW_MAXIMIZED = true;

    // =============== Theme Preferences ===============

    /**
     * Save dark mode preference.
     */
    public static void saveDarkMode(boolean isDarkMode) {
        prefs.putBoolean(KEY_DARK_MODE, isDarkMode);
        System.out.println("💾 Saved dark mode preference: " + isDarkMode);
    }

    /**
     * Load dark mode preference.
     */
    public static boolean loadDarkMode() {
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, DEFAULT_DARK_MODE);
        System.out.println("📖 Loaded dark mode preference: " + isDarkMode);
        return isDarkMode;
    }

    // =============== Animation Speed Preferences ===============

    /**
     * Save animation speed multiplier (0.5 = slow, 1.0 = normal, 2.0 = fast).
     */
    public static void saveAnimationSpeed(double speed) {
        prefs.putDouble(KEY_ANIMATION_SPEED, speed);
        System.out.println("💾 Saved animation speed: " + speed);
    }

    /**
     * Load animation speed multiplier.
     */
    public static double loadAnimationSpeed() {
        return prefs.getDouble(KEY_ANIMATION_SPEED, DEFAULT_ANIMATION_SPEED);
    }

    // =============== Tooltip Preferences ===============

    /**
     * Save tooltip visibility preference.
     */
    public static void saveShowTooltips(boolean show) {
        prefs.putBoolean(KEY_SHOW_TOOLTIPS, show);
    }

    /**
     * Load tooltip visibility preference.
     */
    public static boolean loadShowTooltips() {
        return prefs.getBoolean(KEY_SHOW_TOOLTIPS, DEFAULT_SHOW_TOOLTIPS);
    }

    // =============== Window Preferences ===============

    /**
     * Save window maximized state.
     */
    public static void saveWindowMaximized(boolean maximized) {
        prefs.putBoolean(KEY_WINDOW_MAXIMIZED, maximized);
    }

    /**
     * Load window maximized state.
     */
    public static boolean loadWindowMaximized() {
        return prefs.getBoolean(KEY_WINDOW_MAXIMIZED, DEFAULT_WINDOW_MAXIMIZED);
    }

    // =============== Last Module Tracking ===============

    /**
     * Save last opened module for quick access.
     */
    public static void saveLastModule(String moduleName) {
        if (moduleName != null) {
            prefs.put(KEY_LAST_MODULE, moduleName);
        }
    }

    /**
     * Load last opened module.
     */
    public static String loadLastModule() {
        return prefs.get(KEY_LAST_MODULE, "");
    }

    // =============== Utility Methods ===============

    /**
     * Clear all preferences (reset to defaults).
     */
    public static void clearAll() {
        try {
            prefs.clear();
            System.out.println("🧹 All preferences cleared");
        } catch (Exception e) {
            System.err.println("❌ Failed to clear preferences: " + e.getMessage());
        }
    }

    /**
     * Check if preferences have been initialized.
     */
    public static boolean hasPreferences() {
        try {
            return prefs.keys().length > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
