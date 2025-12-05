package com.comp2042.system;

import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Manages keyboard bindings for game controls.
 * Allows users to customise which keys trigger game actions.
 * Supports multiple keys per action and conflict detection.
 */
public class KeyBindings {

    /** Map of action names to their bound keys. */
    private final Map<String, List<KeyCode>> bindings;

    /**
     * Constructs a KeyBindings manager with default key mappings.
     */
    public KeyBindings() {
        bindings = new HashMap<>();
        loadDefaultBindings();
    }

    /**
     * Loads the default key bindings for all game actions.
     */
    private void loadDefaultBindings() {
        bindings.put("MOVE_LEFT", new ArrayList<>(Arrays.asList(KeyCode.LEFT, KeyCode.A)));
        bindings.put("MOVE_RIGHT", new ArrayList<>(Arrays.asList(KeyCode.RIGHT, KeyCode.D)));
        bindings.put("SOFT_DROP", new ArrayList<>(Arrays.asList(KeyCode.DOWN, KeyCode.S)));
        bindings.put("HARD_DROP", new ArrayList<>(Arrays.asList(KeyCode.SPACE)));
        bindings.put("ROTATE", new ArrayList<>(Arrays.asList(KeyCode.UP, KeyCode.W)));
        bindings.put("ROTATE_LEFT", new ArrayList<>(Arrays.asList(KeyCode.Z, KeyCode.J)));
        bindings.put("ROTATE_RIGHT", new ArrayList<>(Arrays.asList(KeyCode.X, KeyCode.K)));
        bindings.put("HOLD", new ArrayList<>(Arrays.asList(KeyCode.H, KeyCode.C)));
        bindings.put("PAUSE", new ArrayList<>(Arrays.asList(KeyCode.ESCAPE, KeyCode.P)));
    }

    /**
     * Checks if a key is bound to a specific action.
     *
     * @param action the action name
     * @param key    the key to check
     * @return true if the key is bound to the action
     */
    public boolean isKeyBound(String action, KeyCode key) {
        List<KeyCode> keys = bindings.get(action);
        if (keys == null)
            return false;
        return keys.contains(key);
    }

    /**
     * Returns all keys bound to an action.
     *
     * @param action the action name
     * @return list of bound keys (empty if action not found)
     */
    public List<KeyCode> getKeysForAction(String action) {
        List<KeyCode> keys = bindings.get(action);
        if (keys == null)
            return new ArrayList<>();
        return new ArrayList<>(keys);
    }

    /**
     * Rebinds an action to a single key, replacing all existing bindings.
     *
     * @param action the action to rebind
     * @param newKey the new key to bind
     */
    public void rebindKey(String action, KeyCode newKey) {
        if (!bindings.containsKey(action)) {
            bindings.put(action, new ArrayList<>());
        }

        for (String otherAction : bindings.keySet()) {
            bindings.get(otherAction).remove(newKey);
        }

        List<KeyCode> keys = bindings.get(action);
        keys.clear();
        keys.add(newKey);
    }

    /**
     * Adds an additional key binding to an action.
     *
     * @param action the action to add binding to
     * @param key    the key to add
     */
    public void addKeyBinding(String action, KeyCode key) {
        if (!bindings.containsKey(action)) {
            bindings.put(action, new ArrayList<>());
        }

        List<KeyCode> keys = bindings.get(action);
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    /**
     * Removes a key binding from an action.
     *
     * @param action the action to remove binding from
     * @param key    the key to remove
     */
    public void removeKeyBinding(String action, KeyCode key) {
        List<KeyCode> keys = bindings.get(action);
        if (keys != null) {
            keys.remove(key);
        }
    }

    /**
     * Resets all bindings to their default values.
     */
    public void resetToDefaults() {
        bindings.clear();
        loadDefaultBindings();
    }

    /**
     * Returns all available action names.
     *
     * @return set of action names
     */
    public Set<String> getAllActions() {
        return new HashSet<>(bindings.keySet());
    }

    /**
     * Checks if a key is currently used by any action.
     *
     * @param key the key to check
     * @return true if the key is bound to any action
     */
    public boolean isKeyUsed(KeyCode key) {
        for (List<KeyCode> keys : bindings.values()) {
            if (keys.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the action that a key is bound to.
     *
     * @param key the key to look up
     * @return the action name, or null if not bound
     */
    public String getActionForKey(KeyCode key) {
        for (Map.Entry<String, List<KeyCode>> entry : bindings.entrySet()) {
            if (entry.getValue().contains(key)) {
                return entry.getKey();
            }
        }
        return null;
    }
}