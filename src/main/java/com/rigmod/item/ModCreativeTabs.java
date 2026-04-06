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
            .icon(() -> new ItemStack(ModItems.TITANIUM_INGOT.get())) 
            .title(Component.translatable("creativetab.rig_tab"))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.TITANIUM_INGOT.get());
                output.accept(ModItems.STANDARD_LEVEL_1_HELMET.get());
                
                // Add both variants to the tab
                output.accept(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get());
                output.accept(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get());

                // Add leggings to the tab
                output.accept(ModItems.STANDARD_LEVEL_1_LEGGINGS.get());

                // Add the new Engineering Level 2 Helmet to the tab
                output.accept(ModItems.ENGINEERING_LEVEL_2_HELMET.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}