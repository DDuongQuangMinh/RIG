package com.rigmod;

import com.mojang.logging.LogUtils;
import com.rigmod.item.ModItems;
import com.rigmod.item.ModCreativeTabs;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(RigMod.MODID)
public class RigMod
{
    public static final String MODID = "rigmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    // FIXED: Passed IEventBus directly into the constructor to clear the warning
    public RigMod(IEventBus modEventBus) {
        
        // FIXED: Changed "registerItems" to "register" to match your ModItems class
        ModItems.register(modEventBus); 
        
        // FIXED: Added this line so your custom tab actually loads into the game!
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup code here
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Left empty intentionally so your item doesn't duplicate into vanilla tabs
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Code to run when the server starts
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client setup code
        }
    }
}