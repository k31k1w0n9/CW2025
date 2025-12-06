package com.comp2042.events;

/**
 * Represents a movement event in the Tetris game.
 * Encapsulates the type of movement and its source (user or system).
 * This is an immutable class used to pass event information between
 * controllers.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent with the specified type and source.
     *
     * @param eventType   the type of movement event (e.g., LEFT, RIGHT, ROTATE)
     * @param eventSource the source of the event (USER or THREAD)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Returns the type of this movement event.
     *
     * @return the event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Returns the source of this movement event.
     *
     * @return the event source
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
