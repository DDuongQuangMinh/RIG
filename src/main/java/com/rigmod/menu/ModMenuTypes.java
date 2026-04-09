package com.rigmod.menu;

import com.rigmod.RigMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RigMod.MODID);

    public static final RegistryObject<MenuType<RigWorkbenchMenu>> RIG_WORKBENCH_MENU =
            MENUS.register("rig_workbench_menu", () -> IForgeMenuType.create(RigWorkbenchMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}