// File: com/rigmod/client/KeyBindings.java
package com.rigmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_RIGMOD = "key.category.rigmod";
    
    public static final String KEY_CYCLE_VISION = "key.rigmod.cycle_vision";
    public static final String KEY_CYCLE_RADAR = "key.rigmod.cycle_radar";
    // 🔥 NEW: Stable key String ID
    public static final String KEY_TOGGLE_STABLE = "key.rigmod.toggle_stable";
    // 🔥 NEW: Rotation key String IDs
    public static final String KEY_ROTATE_LEFT = "key.rigmod.rotate_left";
    public static final String KEY_ROTATE_RIGHT = "key.rigmod.rotate_right";

    public static final KeyMapping CYCLE_VISION_KEY = new KeyMapping(
            KEY_CYCLE_VISION, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_V, // Sets default key to 'V'
            KEY_CATEGORY_RIGMOD
    );

    public static final KeyMapping CYCLE_RADAR_KEY = new KeyMapping(
            KEY_CYCLE_RADAR, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_R, // Sets default key to 'R'
            KEY_CATEGORY_RIGMOD
    );

    // 🔥 NEW: Stable keyMapping
    public static final KeyMapping TOGGLE_STABLE_KEY = new KeyMapping(
            KEY_TOGGLE_STABLE, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_P, // Sets default key to 'P'
            KEY_CATEGORY_RIGMOD
    );

    // 🔥 NEW: Rotation keyMappings
    public static final KeyMapping ROTATE_LEFT_KEY = new KeyMapping(
            KEY_ROTATE_LEFT, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_Z, // Sets default key to 'Z'
            KEY_CATEGORY_RIGMOD
    );

    public static final KeyMapping ROTATE_RIGHT_KEY = new KeyMapping(
            KEY_ROTATE_RIGHT, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_C, // Sets default key to 'C'
            KEY_CATEGORY_RIGMOD
    );
}