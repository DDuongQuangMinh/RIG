package com.rigmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_RIGMOD = "key.category.rigmod";
    public static final String KEY_CYCLE_VISION = "key.rigmod.cycle_vision";

    public static final KeyMapping CYCLE_VISION_KEY = new KeyMapping(
            KEY_CYCLE_VISION, 
            KeyConflictContext.IN_GAME, 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_V, // Sets default key to 'V'
            KEY_CATEGORY_RIGMOD
    );
}