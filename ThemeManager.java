package com.simulator;

import atlantafx.base.theme.*;
import javafx.application.Application;
import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;
    private volatile boolean isDarkMode = false;
    private volatile String currentTheme = "PrimerLight";
    private final List<Scene> registeredScenes = Collections.synchronizedList(new ArrayList<>());

    public enum ThemeType {
        PRIMER_LIGHT("Primer Light", PrimerLight.class),
        PRIMER_DARK("Primer Dark", PrimerDark.class),
        NORD_LIGHT("Nord Light", NordLight.class),
        NORD_DARK("Nord Dark", NordDark.class),
        CUPERTINO_LIGHT("Cupertino Light", CupertinoLight.class),
        CUPERTINO_DARK("Cupertino Dark", CupertinoDark.class);

        private final String displayName;
        private final Class<? extends Theme> themeClass;

        ThemeType(String displayName, Class<? extends Theme> themeClass) {
            this.displayName = displayName;
            this.themeClass = themeClass;
        }

        public String getDisplayName() { return displayName; }
        public Class<? extends Theme> getThemeClass() { return themeClass; }
    }

    private ThemeManager() {}

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Register a scene for theme updates
     */
    public void registerScene(Scene scene) {
        if (scene != null && !registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            // Apply current theme to newly registered scene
            updateScene(scene);
            System.out.println("✅ Scene registered with ThemeManager (Total: " + registeredScenes.size() + ")");
        }
    }

    /**
     * Unregister a scene (important for memory management)
     */
    public void unregisterScene(Scene scene) {
        if (scene != null && registeredScenes.remove(scene)) {
            System.out.println("🗑️ Scene unregistered from ThemeManager (Remaining: " + registeredScenes.size() + ")");
        }
    }

    /**
     * Clear all registered scenes
     */
    public void clearAllScenes() {
        int count = registeredScenes.size();
        registeredScenes.clear();
        System.out.println("🧹 Cleared " + count + " scenes from ThemeManager");
    }

    /**
     * Get number of registered scenes
     */
    public int getRegisteredSceneCount() {
        return registeredScenes.size();
    }

    /**
     * Update all registered scenes when theme changes
     */
    private void updateAllRegisteredScenes() {
        if (!registeredScenes.isEmpty()) {
            System.out.println("🔄 Updating " + registeredScenes.size() + " registered scenes...");
            // Create a copy to avoid concurrent modification
            List<Scene> scenesToUpdate = new ArrayList<>(registeredScenes);
            for (Scene scene : scenesToUpdate) {
                updateScene(scene);
            }
            System.out.println("✅ All scenes updated successfully");
        }
    }

    /**
     * Update individual scene styling
     */
    private void updateScene(Scene scene) {
        try {
            if (scene != null && scene.getRoot() != null) {
                String themeClass = isDarkMode ? "dark-theme" : "light-theme";
                scene.getRoot().getStyleClass().removeAll("light-theme", "dark-theme");
                scene.getRoot().getStyleClass().add(themeClass);
                System.out.println("🎨 Scene updated with theme class: " + themeClass);
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating scene theme: " + e.getMessage());
        }
    }

    public void setDefaultTheme() {
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            currentTheme = "PrimerLight";
            isDarkMode = false;
            updateAllRegisteredScenes();
            System.out.println("🌟 Default theme applied: " + currentTheme);
        } catch (Exception e) {
            System.err.println("❌ Failed to load default theme: " + e.getMessage());
            fallbackToDefault();
        }
    }

    public void toggleTheme() {
        String previousTheme = currentTheme;
        boolean wasDarkMode = isDarkMode;

        try {
            if (isDarkMode) {
                setLightTheme();
            } else {
                setDarkTheme();
            }

            System.out.println("🎨 Theme toggled: " + previousTheme + " (" +
                    (wasDarkMode ? "Dark" : "Light") + ") → " + currentTheme + " (" +
                    (isDarkMode ? "Dark" : "Light") + ")");
        } catch (Exception e) {
            System.err.println("❌ Error toggling theme: " + e.getMessage());
            fallbackToDefault();
        }
    }

    public void setLightTheme() {
        try {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            currentTheme = "PrimerLight";
            isDarkMode = false;
            updateAllRegisteredScenes();
            System.out.println("☀️ Light theme applied: " + currentTheme);
        } catch (Exception e) {
            System.err.println("❌ Failed to load light theme: " + e.getMessage());
            fallbackToDefault();
        }
    }

    public void setDarkTheme() {
        try {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            currentTheme = "PrimerDark";
            isDarkMode = true;
            updateAllRegisteredScenes();
            System.out.println("🌙 Dark theme applied: " + currentTheme);
        } catch (Exception e) {
            System.err.println("❌ Failed to load dark theme: " + e.getMessage());
            fallbackToDefault();
        }
    }

    public void setTheme(ThemeType themeType) {
        try {
            Theme theme = themeType.getThemeClass().getDeclaredConstructor().newInstance();
            Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
            currentTheme = themeType.name();
            isDarkMode = themeType.name().contains("DARK");
            updateAllRegisteredScenes();
            System.out.println("🎨 Custom theme applied: " + themeType.getDisplayName());
        } catch (Exception e) {
            System.err.println("❌ Failed to load theme " + themeType.getDisplayName() + ": " + e.getMessage());
            fallbackToDefault();
        }
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public ThemeType[] getAvailableThemes() {
        return ThemeType.values();
    }

    /**
     * Get current theme type enum
     */
    public ThemeType getCurrentThemeType() {
        for (ThemeType type : ThemeType.values()) {
            if (type.name().equals(currentTheme)) {
                return type;
            }
        }
        return isDarkMode ? ThemeType.PRIMER_DARK : ThemeType.PRIMER_LIGHT;
    }

    /**
     * Check if a specific theme is available
     */
    public boolean isThemeAvailable(String themeName) {
        for (ThemeType type : ThemeType.values()) {
            if (type.name().equalsIgnoreCase(themeName) ||
                    type.getDisplayName().equalsIgnoreCase(themeName)) {
                return true;
            }
        }
        return false;
    }

    private void fallbackToDefault() {
        try {
            Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
            currentTheme = "Modena (Fallback)";
            isDarkMode = false;
            updateAllRegisteredScenes();
            System.out.println("🔧 Switched to fallback theme: " + currentTheme);
        } catch (Exception e) {
            System.err.println("❌ Critical error: Even fallback theme failed: " + e.getMessage());
        }
    }

    /**
     * Reset theme manager to initial state
     */
    public void reset() {
        clearAllScenes();
        setDefaultTheme();
        System.out.println("🔄 ThemeManager reset to default state");
    }

    @Override
    public String toString() {
        return "ThemeManager{" +
                "currentTheme='" + currentTheme + '\'' +
                ", isDarkMode=" + isDarkMode +
                ", registeredScenes=" + registeredScenes.size() +
                '}';
    }
}
