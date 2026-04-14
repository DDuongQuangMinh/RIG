package com.rigmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_RIGMOD = "key.category.rigmod";
    
    public static final String KEY_CYCLE_VISION = "key.rigmod.cycle_vision";
    public static final String KEY_CYCLE_RADAR = "key.rigmod.cycle_radar";
    // 🔥 NEW: String ID for the Stable Mode key
    public static final String KEY_TOGGLE_STABLE = "key.rigmod.toggle_stable";

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

    // 🔥 NEW: KeyMapping for the Stable Flight Mode
    public static final KeyMapping TOGGLE_STABLE_KEY = new KeyMapping(
            KEY_TOGGLE_STABLE, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_P, // Sets default key to 'P'
            KEY_CATEGORY_RIGMOD
    );
}