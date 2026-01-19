package com.simulator;

/**
 * Interface for controllers and services that need cleanup when their window
 * closes.
 * Implementing this interface allows NavigationService to properly clean up
 * resources.
 */
public interface Cleanable {

    /**
     * Clean up resources when the window is closed.
     * Implementers should:
     * - Unregister from ThemeManager
     * - Stop any running animations
     * - Release any held resources
     */
    void cleanup();
}
