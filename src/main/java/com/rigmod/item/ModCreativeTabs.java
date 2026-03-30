package com.rigmod.item;

import com.rigmod.RigMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RigMod.MODID);

    public static final RegistryObject<CreativeModeTab> RIG_TAB = CREATIVE_MODE_TABS.register("rig_tab", 
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.TITANIUM_INGOT.get())) // Sets Titanium as the tab's icon
            .title(Component.translatable("creativetab.rig_tab"))
            .displayItems((parameters, output) -> {
                // Add your items to the tab here
                output.accept(ModItems.TITANIUM_INGOT.get()); 
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}