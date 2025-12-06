package com.comp2042.events;

/**
 * Enumeration representing the source of a game event.
 * Distinguishes between user-initiated actions and system-generated events.
 */
public enum EventSource {
    /** Event triggered by user input (e.g., key press). */
    USER,
    /** Event triggered by the game loop thread (e.g., automatic piece drop). */
    THREAD
}
