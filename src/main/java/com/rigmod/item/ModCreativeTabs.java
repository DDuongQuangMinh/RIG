package com.rigmod.item;

import com.rigmod.RigMod;
import com.rigmod.block.ModBlocks;
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
                output.accept(ModItems.RAW_TITANIUM.get());
                output.accept(ModBlocks.RAW_TITANIUM_BLOCK.get());
                output.accept(ModBlocks.RIG_WORKBENCH.get());

                output.accept(ModItems.UPGRADE_NODE.get());

                output.accept(ModItems.BATTERY_LEVEL_1.get());
                output.accept(ModItems.BATTERY_LEVEL_2.get());
                output.accept(ModItems.BATTERY_LEVEL_3.get());
                output.accept(ModItems.BATTERY_LEVEL_4.get());
                output.accept(ModItems.BATTERY_LEVEL_5.get());
                output.accept(ModItems.BATTERY_LEVEL_6.get());
                output.accept(ModItems.BATTERY_LEVEL_7.get());
                output.accept(ModItems.STANDARD_LEVEL_1_HELMET.get());
                output.accept(ModItems.ENGINEERING_LEVEL_2_HELMET.get());
                output.accept(ModItems.ENGINEERING_LEVEL_3_HELMET.get());

                output.accept(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get());
                output.accept(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get());
                output.accept(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get());
                output.accept(ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get());
                
                // Add leggings to the tab
                output.accept(ModItems.STANDARD_LEVEL_1_LEGGINGS.get());
                output.accept(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get());
                output.accept(ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get());

                output.accept(ModItems.STANDARD_LEVEL_1_BOOTS.get());
                output.accept(ModItems.ENGINEERING_LEVEL_2_BOOTS.get());

                // Add Gun & Ammo
                output.accept(ModItems.PLASMA_CUTTER.get());
                output.accept(ModItems.PLASMA_ENERGY.get()); // NEW: Added your custom ammo here!
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}