package com.comp2042.data;

/**
 * Immutable data transfer object representing the result of a downward
 * movement.
 * Contains information about any rows that were cleared and the updated view
 * state.
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a new DownData with the specified clear row result and view data.
     *
     * @param clearRow the result of any row clearing operation, or null if no rows
     *                 were cleared
     * @param viewData the updated view data after the move
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Returns the result of the row clearing operation.
     *
     * @return the ClearRow object, or null if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Returns the updated view data after the move.
     *
     * @return the ViewData object
     */
    public ViewData getViewData() {
        return viewData;
    }
}
