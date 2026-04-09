package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rigmod.item.ModItems;
import com.rigmod.menu.RigWorkbenchMenu;
import com.rigmod.network.ModMessages;
import com.rigmod.network.packet.CraftArmorPacket;
import com.rigmod.network.packet.RechargeArmorPacket;
import com.rigmod.network.packet.SyncWorkbenchModePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RigWorkbenchScreen extends AbstractContainerScreen<RigWorkbenchMenu> {

    private int mainMode = 0; 
    
    private Button craftButton;
    private Button rechargeButton;

    private int selectedTab = 0;   
    private int selectedIndex = 0; 

    private final ItemStack[][] categorizedBlueprints = new ItemStack[][]{
        new ItemStack[]{
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get())
        },
        new ItemStack[]{
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get())
        },
        new ItemStack[]{
            new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get())
        }
    };

    private final ItemStack[] tabIcons = new ItemStack[]{
        new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get()),
        new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()),
        new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get())
    };

    public RigWorkbenchScreen(RigWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 400; 
        this.imageHeight = 240;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.craftButton = addRenderableWidget(Button.builder(Component.literal("Craft"), button -> {
            if (countTitanium() >= 8) {
                ModMessages.sendToServer(new CraftArmorPacket(selectedTab, selectedIndex));
            }
            this.setFocused(null); 
        }).bounds(x + 294, y + 205, 90, 20).build()); 

        this.rechargeButton = addRenderableWidget(Button.builder(Component.literal("Inject Power"), button -> {
            ModMessages.sendToServer(new RechargeArmorPacket());
            this.setFocused(null);
        }).bounds(x + 150, y + 180, 100, 20).build());

        ModMessages.sendToServer(new SyncWorkbenchModePacket(this.menu.blockEntity.getBlockPos(), mainMode));
    }

    @Override
    public void removed() {
        super.removed();
        ModMessages.sendToServer(new SyncWorkbenchModePacket(this.menu.blockEntity.getBlockPos(), -1));
    }

    private int countTitanium() {
        int count = 0;
        Inventory inv = Minecraft.getInstance().player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == ModItems.TITANIUM_INGOT.get()) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics); 
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.craftButton.visible = (mainMode == 0);
        this.rechargeButton.visible = (mainMode == 1);

        for (int m = 0; m < 2; m++) {
            int tabX = x - 28;
            int tabY = y + 20 + (m * 35);

            if (mainMode == m) {
                guiGraphics.fill(tabX, tabY, tabX + 30, tabY + 30, 0xFF4A4A4A);
                guiGraphics.fill(tabX + 2, tabY + 2, tabX + 30, tabY + 28, 0xFF222222); 
            } else {
                guiGraphics.fill(tabX + 4, tabY, tabX + 30, tabY + 30, 0xFF333333);
                guiGraphics.fill(tabX + 6, tabY + 2, tabX + 30, tabY + 28, 0xFF181818);
            }

            // THE FIX: Replaced vanilla items with your custom ModItems!
            ItemStack modeIcon = (m == 0) ? new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get()) : new ItemStack(ModItems.BATTERY_LEVEL_1.get());
            guiGraphics.renderItem(modeIcon, tabX + 8, tabY + 6);
        }

        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF4A4A4A); 
        guiGraphics.fill(x + 2, y + 2, x + imageWidth - 2, y + imageHeight - 2, 0xFF222222); 

        if (mainMode == 0) {
            guiGraphics.fill(x + 6, y + 6, x + 160, y + imageHeight - 6, 0xFF333333);
            guiGraphics.fill(x + 7, y + 20, x + 159, y + imageHeight - 7, 0xFF181818);
            
            guiGraphics.fill(x + 164, y + 35, x + 280, y + imageHeight - 6, 0xFF333333);
            guiGraphics.fill(x + 165, y + 36, x + 279, y + imageHeight - 7, 0xFF181818);

            guiGraphics.fill(x + 284, y + 6, x + imageWidth - 6, y + imageHeight - 6, 0xFF333333);
            guiGraphics.fill(x + 285, y + 20, x + imageWidth - 7, y + imageHeight - 7, 0xFF181818);

            guiGraphics.drawCenteredString(this.font, "Preview", x + 83, y + 9, 0xFFFFFF);
            guiGraphics.drawString(this.font, "Ingredient:", x + 290, y + 9, 0xFFFFFF, false);

            for (int t = 0; t < tabIcons.length; t++) {
                int tabX = x + 168 + (t * 26); 
                int tabY = y + 6;
                
                if (t == selectedTab) {
                    guiGraphics.fill(tabX, tabY, tabX + 24, tabY + 24, 0xFF4A4A4A);
                    guiGraphics.fill(tabX + 1, tabY + 1, tabX + 23, tabY + 24, 0xFF2A2A2A); 
                } else {
                    guiGraphics.fill(tabX, tabY, tabX + 24, tabY + 24, 0xFF333333);
                    guiGraphics.fill(tabX + 1, tabY + 1, tabX + 23, tabY + 23, 0xFF1A1A1A);
                }
                guiGraphics.renderItem(tabIcons[t], tabX + 4, tabY + 4);
            }

            ItemStack[] currentList = categorizedBlueprints[selectedTab]; 

            for (int i = 0; i < currentList.length; i++) {
                int itemY = y + 40 + (i * 24); 
                
                if (i == selectedIndex) {
                    guiGraphics.fill(x + 166, itemY - 2, x + 278, itemY + 20, 0xFF00FFFF); 
                }
                
                guiGraphics.renderItem(currentList[i], x + 168, itemY);
                
                String name = currentList[i].getHoverName().getString();
                if (name.length() > 14) name = name.substring(0, 14) + "...";
                
                int textColor = (i == selectedIndex) ? 0x000000 : 0xFFFFFF;
                guiGraphics.drawString(this.font, name, x + 188, itemY + 5, textColor, false);
            }

            if (selectedIndex >= currentList.length) selectedIndex = 0; 
            ItemStack selectedItem = currentList[selectedIndex];
            
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(x + 83, y + 115, 100); 
            pose.scale(4.0F, 4.0F, 4.0F); 
            guiGraphics.renderItem(selectedItem, -8, -8);
            pose.popPose();

            pose.pushPose();
            pose.translate(x + 83, y + 210, 0); 
            pose.scale(0.9F, 0.9F, 1.0F); 
            guiGraphics.drawCenteredString(this.font, selectedItem.getHoverName(), 0, 0, 0xFFFFFF);
            pose.popPose();

            int requiredAmount = 8; 
            int playerTitaniumCount = countTitanium();
            int countColor = (playerTitaniumCount >= requiredAmount) ? 0xFF55FF55 : 0xFFFF5555;

            ItemStack requiredItem = new ItemStack(ModItems.TITANIUM_INGOT.get());
            guiGraphics.renderItem(requiredItem, x + 290, y + 30);
            guiGraphics.drawString(this.font, playerTitaniumCount + " / " + requiredAmount, x + 310, y + 34, countColor, false); 
            
            guiGraphics.drawString(this.font, "Titanium Ingot", x + 290, y + 55, 0xFFAAAAAA, false);
            guiGraphics.drawString(this.font, "Count: 1", x + 290, y + 185, 0xFFFFFF, false);
        }
        else if (mainMode == 1) {
            guiGraphics.fill(x + 60, y + 20, x + 340, y + imageHeight - 20, 0xFF333333);
            guiGraphics.fill(x + 61, y + 34, x + 339, y + imageHeight - 21, 0xFF181818);
            guiGraphics.drawCenteredString(this.font, "Power Injection Matrix", x + 200, y + 23, 0xFF00E5FF);

            ItemStack equippedChest = Minecraft.getInstance().player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            
            if (equippedChest.getItem() instanceof com.rigmod.item.Custom3DArmorItem) {
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(x + 200, y + 100, 100); 
                pose.scale(5.0F, 5.0F, 5.0F); 
                guiGraphics.renderItem(equippedChest, -8, -8);
                pose.popPose();

                int power = equippedChest.getOrCreateTag().getInt("RigPower");
                
                this.rechargeButton.active = (power < 100);

                int powerColor = (power == 100) ? 0xFF55FFFF : (power > 50) ? 0xFF55FF55 : (power > 20) ? 0xFFFFFF55 : 0xFFFF5555;

                guiGraphics.drawCenteredString(this.font, "Target: " + equippedChest.getHoverName().getString(), x + 200, y + 140, 0xFFFFFF);
                guiGraphics.drawCenteredString(this.font, "Core Power: " + power + "%", x + 200, y + 155, powerColor);
                
                guiGraphics.renderItem(new ItemStack(ModItems.BATTERY_LEVEL_1.get()), x + 125, y + 180);
                
            } else {
                this.rechargeButton.active = false;

                guiGraphics.drawCenteredString(this.font, "ERROR: NO RIG SUIT DETECTED", x + 200, y + 110, 0xFFFF5555);
                guiGraphics.drawCenteredString(this.font, "Please equip a RIG Chestplate to recharge.", x + 200, y + 125, 0xFFAAAAAA);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int m = 0; m < 2; m++) {
            int tabX = x - 28;
            int tabY = y + 20 + (m * 35);
            if (mouseX >= tabX && mouseX <= tabX + 28 && mouseY >= tabY && mouseY <= tabY + 30) {
                mainMode = m; 
                ModMessages.sendToServer(new SyncWorkbenchModePacket(this.menu.blockEntity.getBlockPos(), mainMode));
                return true;
            }
        }

        if (mainMode == 0) {
            for (int t = 0; t < tabIcons.length; t++) {
                int tabX = x + 168 + (t * 26);
                int tabY = y + 6;
                if (mouseX >= tabX && mouseX <= tabX + 24 && mouseY >= tabY && mouseY <= tabY + 24) {
                    selectedTab = t;     
                    selectedIndex = 0;   
                    return true;
                }
            }

            if (mouseX >= x + 166 && mouseX <= x + 278) {
                ItemStack[] currentList = categorizedBlueprints[selectedTab];
                for (int i = 0; i < currentList.length; i++) {
                    int itemY = y + 40 + (i * 24);
                    if (mouseY >= itemY - 2 && mouseY <= itemY + 20) {
                        selectedIndex = i;
                        return true;
                    }
                }
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}