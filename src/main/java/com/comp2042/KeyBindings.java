package com.comp2042;

import javafx.scene.input.KeyCode;
import java.util.*;

public class KeyBindings {

    private final Map<String, List<KeyCode>> bindings;

    public KeyBindings() {
        bindings = new HashMap<>();
        loadDefaultBindings();
    }

    private void loadDefaultBindings() {
        // Movement
        bindings.put("MOVE_LEFT", new ArrayList<>(Arrays.asList(KeyCode.LEFT, KeyCode.A)));
        bindings.put("MOVE_RIGHT", new ArrayList<>(Arrays.asList(KeyCode.RIGHT, KeyCode.D)));
        bindings.put("SOFT_DROP", new ArrayList<>(Arrays.asList(KeyCode.DOWN, KeyCode.S)));
        bindings.put("HARD_DROP", new ArrayList<>(Arrays.asList(KeyCode.SPACE)));

        // Rotation
        bindings.put("ROTATE", new ArrayList<>(Arrays.asList(KeyCode.UP, KeyCode.W)));
        bindings.put("ROTATE_LEFT", new ArrayList<>(Arrays.asList(KeyCode.Z, KeyCode.J)));
        bindings.put("ROTATE_RIGHT", new ArrayList<>(Arrays.asList(KeyCode.X, KeyCode.K)));

        // Special actions
        bindings.put("HOLD", new ArrayList<>(Arrays.asList(KeyCode.H, KeyCode.C)));
        bindings.put("PAUSE", new ArrayList<>(Arrays.asList(KeyCode.ESCAPE, KeyCode.P)));
    }

    // Check if a key is bound to an action
    public boolean isKeyBound(String action, KeyCode key) {
        List<KeyCode> keys = bindings.get(action);
        if (keys == null) return false;
        return keys.contains(key);
    }

    // Get all keys bound to an action
    public List<KeyCode> getKeysForAction(String action) {
        List<KeyCode> keys = bindings.get(action);
        if (keys == null) return new ArrayList<>();
        return new ArrayList<>(keys);
    }

    // Rebind a key for an action (replaces all existing bindings for that action)
    public void rebindKey(String action, KeyCode newKey) {
        if (!bindings.containsKey(action)) {
            bindings.put(action, new ArrayList<>());
        }

        // Remove the key from any other action it might be bound to
        for (String otherAction : bindings.keySet()) {
            bindings.get(otherAction).remove(newKey);
        }

        // Clear existing bindings for this action and add the new key
        List<KeyCode> keys = bindings.get(action);
        keys.clear();
        keys.add(newKey);
    }

    // Add a key binding to an action (keeps existing bindings)
    public void addKeyBinding(String action, KeyCode key) {
        if (!bindings.containsKey(action)) {
            bindings.put(action, new ArrayList<>());
        }

        List<KeyCode> keys = bindings.get(action);
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    // Remove a key binding from an action
    public void removeKeyBinding(String action, KeyCode key) {
        List<KeyCode> keys = bindings.get(action);
        if (keys != null) {
            keys.remove(key);
        }
    }

    // Reset all bindings to defaults
    public void resetToDefaults() {
        bindings.clear();
        loadDefaultBindings();
    }

    // Get all available actions
    public Set<String> getAllActions() {
        return new HashSet<>(bindings.keySet());
    }

    // Check if a key is used by any action
    public boolean isKeyUsed(KeyCode key) {
        for (List<KeyCode> keys : bindings.values()) {
            if (keys.contains(key)) {
                return true;
            }
        }
        return false;
    }

    // Get the action that a key is bound to (returns null if not bound)
    public String getActionForKey(KeyCode key) {
        for (Map.Entry<String, List<KeyCode>> entry : bindings.entrySet()) {
            if (entry.getValue().contains(key)) {
                return entry.getKey();
            }
        }
        return null;
    }
}