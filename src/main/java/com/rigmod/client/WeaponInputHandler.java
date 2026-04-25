package com.rigmod.client;

import com.rigmod.RigMod;
import com.rigmod.item.PlasmaCutterItem;
import com.rigmod.network.ModMessages;
import com.rigmod.network.packet.FireWeaponPacket;
import com.rigmod.network.packet.ReloadWeaponPacket;
import com.rigmod.network.packet.RotateBladePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RigMod.MODID, value = Dist.CLIENT)
public class WeaponInputHandler {

    private static boolean spaceWasDown = false;

    // Hides vanilla arms so only your GeckoLib arms are visible
    @SubscribeEvent
    public static void onRenderArm(RenderArmEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getMainHandItem().getItem() instanceof PlasmaCutterItem) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof PlasmaCutterItem)) return;

        // R KEY: RELOAD
        // CLEANUP: We can just ask the event what key was pressed instead of checking the whole window!
        if (event.getKey() == GLFW.GLFW_KEY_R && event.getAction() == GLFW.GLFW_PRESS) {
            ModMessages.sendToServer(new ReloadWeaponPacket());
        }

        // SPACE KEY: ROTATE BLADE
        boolean isSpaceDown = mc.options.keyJump.isDown();
        if (isSpaceDown && !spaceWasDown) {
            if (player.isUsingItem()) { 
                ModMessages.sendToServer(new RotateBladePacket());
                mc.options.keyJump.setDown(false); // Stops the player from actually jumping
            }
        }
        spaceWasDown = isSpaceDown;
    }

    // CLEANUP: Using "MouseButton.Pre" is much safer for completely cancelling vanilla punch animations.
    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null || mc.screen != null) return;

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof PlasmaCutterItem)) return;

        // LEFT CLICK: FIRE WEAPON
        // CLEANUP: Using the actual GLFW constant for the left mouse button
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && event.getAction() == GLFW.GLFW_PRESS) {
            if (player.isUsingItem()) {
                ModMessages.sendToServer(new FireWeaponPacket());
                event.setCanceled(true); // Cancels the item punch swing
            }
        }
    }
}