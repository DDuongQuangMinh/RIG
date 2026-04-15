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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RigWorkbenchScreen extends AbstractContainerScreen<RigWorkbenchMenu> {

    private int mainMode = 0; 
    private int subMode = 0; 
    
    private Button craftButton;
    private Button rechargeButton;
    private Button applyNodeButton; 
    private Button btnScrollLeft;  
    private Button btnScrollRight; 

    private int selectedTab = 0;   
    private int selectedIndex = 0; 
    private int tabStartIndex = 0; 
    private final int MAX_VISIBLE_TABS = 4; 

    private float batteryScrollOffset = 0.0f;
    private int batteryStartIndex = 0;
    private int selectedBatteryIndex = -1; 
    
    private static class UpgradeNode {
        int id, x, y;
        String label;
        int[] links;
        boolean purchased;
        public UpgradeNode(int id, int x, int y, String label, boolean purchased, int... links) {
            this.id = id; this.x = x; this.y = y; this.label = label; this.purchased = purchased; this.links = links;
        }
    }
    
    private int selectedUpgradeNode = -1;
    private final UpgradeNode[] suitNodes = new UpgradeNode[]{
        new UpgradeNode(0, 30, 110, "RIG", true, 1),
        new UpgradeNode(1, 70, 110, "HP", false, 2, 4, 7),
        new UpgradeNode(2, 110, 110, "", false, 3),
        new UpgradeNode(3, 150, 110, "ARM", false, 10),
        new UpgradeNode(4, 70, 70, "", false, 5),
        new UpgradeNode(5, 110, 70, "PWR", false, 6),
        new UpgradeNode(6, 150, 70, "SPD", false),
        new UpgradeNode(7, 70, 150, "HP", false, 8),
        new UpgradeNode(8, 110, 150, "", false, 9),
        new UpgradeNode(9, 150, 150, "SP1", false),
        new UpgradeNode(10, 190, 110, "", false, 11),
        new UpgradeNode(11, 230, 110, "CAP", false, 12),
        new UpgradeNode(12, 270, 110, "ARM", false)
    };

    private final ItemStack[] batteryList = new ItemStack[]{
        new ItemStack(ModItems.BATTERY_LEVEL_1.get()),
        new ItemStack(ModItems.BATTERY_LEVEL_2.get()), 
        new ItemStack(ModItems.BATTERY_LEVEL_3.get()),
        new ItemStack(ModItems.BATTERY_LEVEL_4.get()),
        new ItemStack(ModItems.BATTERY_LEVEL_5.get()),
        new ItemStack(ModItems.BATTERY_LEVEL_6.get()),
        new ItemStack(ModItems.BATTERY_LEVEL_7.get())
    };

    private final int[] powerValues = {5, 7, 12, 15, 20, 30, 40};

    private final ItemStack[][] categorizedBlueprints = new ItemStack[][]{
        new ItemStack[]{ // Tab 0: Helmets
            new ItemStack(ModItems.ENGINEERING_LEVEL_3_HELMET.get()),
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get())
        },
        new ItemStack[]{ // Tab 1: Chestplates
            new ItemStack(ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get()), 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get())
        },
        new ItemStack[]{ // Tab 2: Leggings
            new ItemStack(ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get()), 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get())
        },
        new ItemStack[]{ // Tab 3: Boots
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_BOOTS.get()), 
            new ItemStack(ModItems.STANDARD_LEVEL_1_BOOTS.get())
        },
        new ItemStack[]{ // Tab 4: Batteries
            new ItemStack(ModItems.BATTERY_LEVEL_1.get()),
            new ItemStack(ModItems.BATTERY_LEVEL_2.get()),
            new ItemStack(ModItems.BATTERY_LEVEL_3.get()),
            new ItemStack(ModItems.BATTERY_LEVEL_4.get()),
            new ItemStack(ModItems.BATTERY_LEVEL_5.get()),
            new ItemStack(ModItems.BATTERY_LEVEL_6.get()),
            new ItemStack(ModItems.BATTERY_LEVEL_7.get())
        }
    };

    private final ItemStack[] tabIcons = new ItemStack[]{
        new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get()),
        new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()),
        new ItemStack(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get()),
        new ItemStack(ModItems.ENGINEERING_LEVEL_2_BOOTS.get()),
        new ItemStack(ModItems.BATTERY_LEVEL_3.get()) 
    };

    public RigWorkbenchScreen(RigWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 440; 
        this.imageHeight = 260;
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.craftButton = addRenderableWidget(Button.builder(Component.literal("Craft"), button -> {
            ModMessages.sendToServer(new CraftArmorPacket(selectedTab, selectedIndex));
            this.setFocused(null); 
        }).bounds(x + 329, y + 225, 90, 20).build()); 

        this.rechargeButton = addRenderableWidget(Button.builder(Component.literal("Inject Power"), button -> {
            if (selectedBatteryIndex != -1) {
                ModMessages.sendToServer(new RechargeArmorPacket(selectedBatteryIndex)); 
            }
            this.setFocused(null);
        }).bounds(x + 58, y + 225, 100, 20).build()); 

        this.applyNodeButton = addRenderableWidget(Button.builder(Component.literal("Apply Node"), button -> {
            if (selectedUpgradeNode != -1 && !suitNodes[selectedUpgradeNode].purchased) {
                suitNodes[selectedUpgradeNode].purchased = true;
                Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.2F);
            }
            this.setFocused(null);
        }).bounds(x + 329, y + 225, 90, 20).build()); 

        this.btnScrollLeft = addRenderableWidget(Button.builder(Component.literal("<"), button -> {
            if (tabStartIndex > 0) tabStartIndex--;
            this.setFocused(null);
        }).bounds(x + 184, y + 6, 14, 24).build());

        this.btnScrollRight = addRenderableWidget(Button.builder(Component.literal(">"), button -> {
            if (tabStartIndex < tabIcons.length - MAX_VISIBLE_TABS) tabStartIndex++;
            this.setFocused(null);
        }).bounds(x + 296, y + 6, 14, 24).build());

        ModMessages.sendToServer(new SyncWorkbenchModePacket(this.menu.blockEntity.getBlockPos(), mainMode));
    }

    @Override
    public void removed() {
        super.removed();
        ModMessages.sendToServer(new SyncWorkbenchModePacket(this.menu.blockEntity.getBlockPos(), -1));
    }

    private int countItem(Item item) {
        int count = 0;
        Inventory inv = Minecraft.getInstance().player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private ItemStack[] getRecipe(Item item) {
        if (item == ModItems.STANDARD_LEVEL_1_HELMET.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(ModItems.BATTERY_LEVEL_1.get(), 2), new ItemStack(Items.ENDER_EYE, 1) };
        if (item == ModItems.ENGINEERING_LEVEL_2_HELMET.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 7), new ItemStack(ModItems.BATTERY_LEVEL_2.get(), 2), new ItemStack(Items.ENDER_EYE, 3) };
        if (item == ModItems.ENGINEERING_LEVEL_3_HELMET.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 10), new ItemStack(ModItems.BATTERY_LEVEL_3.get(), 1), new ItemStack(Items.BLAZE_ROD, 5), new ItemStack(Items.NETHERITE_INGOT, 3) };
        
        if (item == ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get()) return new ItemStack[]{ new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get(), 1), new ItemStack(ModItems.TITANIUM_INGOT.get(), 12), new ItemStack(ModItems.BATTERY_LEVEL_4.get(), 1), new ItemStack(Items.NETHERITE_INGOT, 4) };
        if (item == ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 10), new ItemStack(Items.QUARTZ, 28), new ItemStack(ModItems.BATTERY_LEVEL_5.get(), 5), new ItemStack(Items.DIAMOND, 12), new ItemStack(Items.NETHERITE_INGOT, 3) };
        if (item == ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(Items.COPPER_INGOT, 7), new ItemStack(Items.REDSTONE, 5), new ItemStack(Items.QUARTZ, 5) };
        if (item == ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(Items.QUARTZ, 12), new ItemStack(Items.REDSTONE, 7), new ItemStack(Items.DIAMOND, 3) };
        
        if (item == ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get()) return new ItemStack[]{ new ItemStack(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get(), 1), new ItemStack(Items.NETHERITE_INGOT, 4), new ItemStack(ModItems.BATTERY_LEVEL_3.get(), 1), new ItemStack(Items.DIAMOND, 8) };
        if (item == ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get()) return new ItemStack[]{ new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get(), 1), new ItemStack(Items.QUARTZ, 12), new ItemStack(ModItems.BATTERY_LEVEL_2.get(), 2), new ItemStack(Items.DIAMOND, 5), new ItemStack(Items.IRON_INGOT, 12) };
        if (item == ModItems.STANDARD_LEVEL_1_LEGGINGS.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 4), new ItemStack(Items.BLACK_DYE, 3), new ItemStack(Items.QUARTZ, 6) };
        
        if (item == ModItems.ENGINEERING_LEVEL_2_BOOTS.get()) return new ItemStack[]{ new ItemStack(ModItems.STANDARD_LEVEL_1_BOOTS.get(), 1), new ItemStack(ModItems.TITANIUM_INGOT.get(), 4), new ItemStack(ModItems.BATTERY_LEVEL_2.get(), 2), new ItemStack(Items.IRON_INGOT, 4) };
        if (item == ModItems.STANDARD_LEVEL_1_BOOTS.get()) return new ItemStack[]{ new ItemStack(Items.QUARTZ, 4), new ItemStack(Items.REDSTONE, 2) };

        if (item == ModItems.BATTERY_LEVEL_1.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 1), new ItemStack(Items.REDSTONE, 4) };
        if (item == ModItems.BATTERY_LEVEL_2.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 2), new ItemStack(Items.REDSTONE, 8) };
        if (item == ModItems.BATTERY_LEVEL_3.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 3), new ItemStack(Items.REDSTONE, 12) };
        if (item == ModItems.BATTERY_LEVEL_4.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 4), new ItemStack(Items.REDSTONE, 16), new ItemStack(Items.QUARTZ, 1) };
        if (item == ModItems.BATTERY_LEVEL_5.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 5), new ItemStack(Items.REDSTONE, 20), new ItemStack(Items.QUARTZ, 2) };
        if (item == ModItems.BATTERY_LEVEL_6.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 6), new ItemStack(Items.REDSTONE, 24), new ItemStack(Items.QUARTZ, 3) };
        if (item == ModItems.BATTERY_LEVEL_7.get()) return new ItemStack[]{ new ItemStack(ModItems.TITANIUM_INGOT.get(), 7), new ItemStack(Items.REDSTONE, 28), new ItemStack(Items.QUARTZ, 4) };

        return new ItemStack[0];
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (mainMode == 0) {
            int x = (width - imageWidth) / 2;
            int y = (height - imageHeight) / 2;
            if (mouseX >= x + 184 && mouseX <= x + 310 && mouseY >= y + 4 && mouseY <= y + 32) {
                if (delta > 0 && tabStartIndex > 0) tabStartIndex--;
                else if (delta < 0 && tabStartIndex < tabIcons.length - MAX_VISIBLE_TABS) tabStartIndex++;
                return true;
            }
        }

        if (mainMode == 1 && subMode == 0) {
            int maxScroll = batteryList.length - 5; 
            if (maxScroll > 0) {
                this.batteryScrollOffset = (float)((double)this.batteryScrollOffset - delta / (double)maxScroll);
                this.batteryScrollOffset = Mth.clamp(this.batteryScrollOffset, 0.0F, 1.0F);
                this.batteryStartIndex = (int)((double)(this.batteryScrollOffset * (float)maxScroll) + 0.5D);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
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
        this.rechargeButton.visible = (mainMode == 1 && subMode == 0); 
        this.applyNodeButton.visible = (mainMode == 1 && subMode == 1);
        
        this.btnScrollLeft.visible = (mainMode == 0);
        this.btnScrollRight.visible = (mainMode == 0);
        this.btnScrollLeft.active = (tabStartIndex > 0);
        this.btnScrollRight.active = (tabStartIndex < tabIcons.length - MAX_VISIBLE_TABS);

        // 🟢 LEFT SIDE: MAIN TABS
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

            ItemStack modeIcon = (m == 0) ? new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get()) : new ItemStack(ModItems.BATTERY_LEVEL_2.get());
            guiGraphics.renderItem(modeIcon, tabX + 8, tabY + 6);
        }

        // 🟢 RIGHT SIDE: SUB TABS (Only visible in Power Mode)
        if (mainMode == 1) {
            for (int s = 0; s < 2; s++) {
                int subTabX = x + imageWidth; // Attached to the right edge!
                int subTabY = y + 20 + (s * 35);

                if (subMode == s) {
                    guiGraphics.fill(subTabX, subTabY, subTabX + 28, subTabY + 30, 0xFF4A4A4A);
                    guiGraphics.fill(subTabX, subTabY + 2, subTabX + 26, subTabY + 28, 0xFF222222); 
                } else {
                    guiGraphics.fill(subTabX, subTabY, subTabX + 24, subTabY + 30, 0xFF333333);
                    guiGraphics.fill(subTabX, subTabY + 2, subTabX + 22, subTabY + 28, 0xFF181818); 
                }

                ItemStack subIcon = (s == 0) ? new ItemStack(ModItems.BATTERY_LEVEL_1.get()) : new ItemStack(ModItems.UPGRADE_NODE.get());
                int iconX = subTabX + 4;
                if (subMode != s) iconX -= 2;
                guiGraphics.renderItem(subIcon, iconX, subTabY + 6);
            }
        }

        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF4A4A4A); 
        guiGraphics.fill(x + 2, y + 2, x + imageWidth - 2, y + imageHeight - 2, 0xFF222222); 

        // ==========================================
        // MODE 0: CRAFTING 
        // ==========================================
        if (mainMode == 0) {
            guiGraphics.fill(x + 6, y + 6, x + 180, y + imageHeight - 6, 0xFF333333);
            guiGraphics.fill(x + 7, y + 20, x + 179, y + imageHeight - 7, 0xFF181818);
            
            guiGraphics.fill(x + 184, y + 35, x + 310, y + imageHeight - 6, 0xFF333333);
            guiGraphics.fill(x + 185, y + 36, x + 309, y + imageHeight - 7, 0xFF181818);

            guiGraphics.fill(x + 314, y + 6, x + imageWidth - 6, y + imageHeight - 6, 0xFF333333);
            guiGraphics.fill(x + 315, y + 20, x + imageWidth - 7, y + imageHeight - 7, 0xFF181818);

            guiGraphics.drawCenteredString(this.font, "Preview", x + 93, y + 9, 0xFFFFFF);
            guiGraphics.drawString(this.font, "Requirements:", x + 320, y + 9, 0xFFFFFF, false);

            for (int i = 0; i < Math.min(MAX_VISIBLE_TABS, tabIcons.length - tabStartIndex); i++) {
                int t = tabStartIndex + i;
                int tabX = x + 200 + (i * 24); 
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
                int itemY = y + 42 + (i * 26); 
                if (i == selectedIndex) guiGraphics.fill(x + 186, itemY - 2, x + 308, itemY + 22, 0xFF00FFFF); 
                
                guiGraphics.renderItem(currentList[i], x + 188, itemY + 1);
                String name = currentList[i].getHoverName().getString().replace("item.rigmod.", "");
                if (name.length() > 16) name = name.substring(0, 16) + "...";
                
                int textColor = (i == selectedIndex) ? 0x000000 : 0xFFFFFF;
                guiGraphics.drawString(this.font, name, x + 208, itemY + 6, textColor, false);
            }

            if (selectedIndex >= currentList.length) selectedIndex = 0; 
            ItemStack selectedItem = currentList[selectedIndex];
            
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.translate(x + 93, y + 125, 100); 
            pose.scale(4.5F, 4.5F, 4.5F); 
            guiGraphics.renderItem(selectedItem, -8, -8);
            pose.popPose();

            pose.pushPose();
            pose.translate(x + 93, y + 230, 0); 
            pose.scale(0.9F, 0.9F, 1.0F); 
            String dName = selectedItem.getHoverName().getString().replace("item.rigmod.", "");
            guiGraphics.drawCenteredString(this.font, dName, 0, 0, 0xFFFFFF);
            pose.popPose();

            ItemStack[] recipe = getRecipe(selectedItem.getItem());
            int ingY = y + 30;
            boolean canCraft = true;

            for (ItemStack ingredient : recipe) {
                int playerHas = countItem(ingredient.getItem());
                int required = ingredient.getCount();
                int countColor = (playerHas >= required) ? 0xFF55FF55 : 0xFFFF5555;
                if (playerHas < required) canCraft = false;
                guiGraphics.renderItem(ingredient, x + 320, ingY);
                guiGraphics.drawString(this.font, playerHas + " / " + required, x + 342, ingY + 4, countColor, false); 
                ingY += 24; 
            }
            this.craftButton.active = canCraft; 
        }
        
        // ==========================================
        // MODE 1: POWER INJECTION & SUIT MODULES
        // ==========================================
        else if (mainMode == 1) {
            
            // --- SUB-MODE 0: BATTERIES ---
            if (subMode == 0) {
                guiGraphics.fill(x + 6, y + 6, x + 210, y + imageHeight - 6, 0xFF333333);
                guiGraphics.fill(x + 7, y + 7, x + 209, y + imageHeight - 7, 0xFF181818);
                
                guiGraphics.fill(x + 242, y + 6, x + 434, y + imageHeight - 6, 0xFF333333);
                guiGraphics.fill(x + 243, y + 7, x + 433, y + imageHeight - 7, 0xFF181818);

                guiGraphics.drawCenteredString(this.font, "Power Injection Matrix", x + 108, y + 15, 0xFF00E5FF);
                ItemStack equippedChest = Minecraft.getInstance().player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
                
                if (equippedChest.getItem() instanceof com.rigmod.item.Custom3DArmorItem) {
                    PoseStack pose = guiGraphics.pose();
                    pose.pushPose();
                    pose.translate(x + 108, y + 95, 100); 
                    pose.scale(4.5F, 4.5F, 4.5F); 
                    guiGraphics.renderItem(equippedChest, -8, -8);
                    pose.popPose();

                    int power = equippedChest.getOrCreateTag().getInt("RigPower");
                    
                    boolean playerOwnsSelectedBattery = false;
                    if (selectedBatteryIndex != -1) {
                        playerOwnsSelectedBattery = countItem(batteryList[selectedBatteryIndex].getItem()) > 0;
                    }
                    this.rechargeButton.active = (power < 100 && playerOwnsSelectedBattery);
                    
                    int powerColor = (power == 100) ? 0xFF55FFFF : (power > 50) ? 0xFF55FF55 : (power > 20) ? 0xFFFFFF55 : 0xFFFF5555;

                    guiGraphics.drawCenteredString(this.font, equippedChest.getHoverName().getString(), x + 108, y + 160, 0xFFFFFF);
                    guiGraphics.drawCenteredString(this.font, "Core Power: " + power + "%", x + 108, y + 175, powerColor);
                    
                    int barWidth = 140;
                    int barHeight = 8;
                    int barX = x + 108 - (barWidth / 2); 
                    int barY = y + 190;

                    guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);
                    guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
                    
                    if (power > 0) {
                        int fillWidth = (int) ((power / 100.0f) * barWidth);
                        guiGraphics.fill(barX, barY, barX + fillWidth, barY + barHeight, powerColor);
                    }

                } else {
                    this.rechargeButton.active = false;
                    guiGraphics.drawCenteredString(this.font, "ERROR: NO RIG SUIT", x + 108, y + 110, 0xFFFF5555);
                    guiGraphics.drawCenteredString(this.font, "Please equip Chestplate.", x + 108, y + 125, 0xFFAAAAAA);
                }

                guiGraphics.drawCenteredString(this.font, "Available Power Cores", x + 338, y + 15, 0xFF00E5FF);
                int listY = y + 35;
                int listX = x + 250; 
                
                for (int i = 0; i < 5; i++) {
                    int batteryIndex = batteryStartIndex + i;
                    
                    if (batteryIndex < batteryList.length) {
                        int rowY = listY + (i * 40); 
                        
                        if (batteryIndex == selectedBatteryIndex) {
                            guiGraphics.fill(listX - 1, rowY - 1, listX + 166, rowY + 36, 0xFF00FFFF);
                        }
                        
                        guiGraphics.fill(listX, rowY, listX + 165, rowY + 35, 0xFF4A4A4A);
                        guiGraphics.fill(listX + 2, rowY + 2, listX + 163, rowY + 33, 0xFF222222);
                        
                        ItemStack currentBattery = batteryList[batteryIndex];
                        guiGraphics.renderItem(currentBattery, listX + 6, rowY + 9);
                        
                        String bName = currentBattery.getHoverName().getString().replace("item.rigmod.", "");
                        if (bName.length() > 14) bName = bName.substring(0, 14) + "...";
                        guiGraphics.drawString(this.font, bName, listX + 28, rowY + 9, 0xFFFFFF, false);
                        
                        int ownedCount = countItem(currentBattery.getItem());
                        int countColor = (ownedCount > 0) ? 0xFF55FF55 : 0xFFFF5555;
                        guiGraphics.drawString(this.font, "Own: " + ownedCount, listX + 28, rowY + 21, countColor, false);
                        
                        guiGraphics.drawString(this.font, "+" + powerValues[batteryIndex] + "%", listX + 135, rowY + 14, 0xFF55FFFF, false);
                    }
                }

                int scrollTrackX = x + 420;
                int scrollTrackY = y + 35;
                guiGraphics.fill(scrollTrackX, scrollTrackY, scrollTrackX + 6, scrollTrackY + 195, 0xFF333333);
                int maxScroll = batteryList.length - 5;
                int handleHeight = 40; 
                int scrollHandleY = scrollTrackY + (int)(batteryScrollOffset * (195 - handleHeight));
                guiGraphics.fill(scrollTrackX, scrollHandleY, scrollTrackX + 6, scrollHandleY + handleHeight, 0xFFAAAAAA);
            } 
            // --- SUB-MODE 1: DEAD SPACE UPGRADE NODES ---
            else if (subMode == 1) {
                guiGraphics.fill(x + 6, y + 6, x + 130, y + imageHeight - 6, 0xFF333333);
                guiGraphics.fill(x + 7, y + 7, x + 129, y + imageHeight - 7, 0xFF181818);
                
                guiGraphics.fill(x + 134, y + 6, x + 434, y + imageHeight - 6, 0xFF333333);
                guiGraphics.fill(x + 135, y + 7, x + 433, y + imageHeight - 7, 0xFF0D1117); 

                int nodesOwned = countItem(ModItems.UPGRADE_NODE.get());
                guiGraphics.drawCenteredString(this.font, "NODES", x + 68, y + 15, 0xFF00E5FF);
                
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(x + 68, y + 50, 0); 
                pose.scale(2.0F, 2.0F, 2.0F); 
                guiGraphics.renderItem(new ItemStack(ModItems.UPGRADE_NODE.get()), -8, -8);
                pose.popPose();
                
                guiGraphics.drawCenteredString(this.font, "x " + nodesOwned, x + 68, y + 70, 0xFFFFFF);

                guiGraphics.drawString(this.font, "RIG SUIT STATS", x + 15, y + 105, 0xFF00E5FF, false);
                guiGraphics.drawString(this.font, "HP Capacity:  100", x + 15, y + 125, 0xFFAAAAAA, false);
                guiGraphics.drawString(this.font, "Armor Plating: 25", x + 15, y + 140, 0xFFAAAAAA, false);
                guiGraphics.drawString(this.font, "Power Max:    50", x + 15, y + 155, 0xFFAAAAAA, false);

                int treeX = x + 135;
                int treeY = y + 7;

                for (UpgradeNode node : suitNodes) {
                    for (int targetId : node.links) {
                        UpgradeNode target = suitNodes[targetId];
                        int startX = treeX + node.x;
                        int startY = treeY + node.y;
                        int endX = treeX + target.x;
                        int endY = treeY + target.y;
                        
                        int lineColor = (node.purchased && target.purchased) ? 0xFF00E5FF : 0xFF2A3A4A;
                        guiGraphics.fill(Math.min(startX, endX) - 1, Math.min(startY, endY) - 1, 
                                         Math.max(startX, endX) + 1, Math.max(startY, endY) + 1, lineColor);
                    }
                }
                
                for (UpgradeNode node : suitNodes) {
                    int nx = treeX + node.x;
                    int ny = treeY + node.y;
                    int color = node.purchased ? 0xFF00E5FF : 0xFF556677;
                    
                    if (selectedUpgradeNode == node.id) guiGraphics.fill(nx - 10, ny - 10, nx + 10, ny + 10, 0x88FFFFFF);
                    guiGraphics.fill(nx - 7, ny - 7, nx + 7, ny + 7, 0xFF0D1117);
                    guiGraphics.fill(nx - 5, ny - 5, nx + 5, ny + 5, color);
                    if (node.purchased) guiGraphics.fill(nx - 2, ny - 2, nx + 2, ny + 2, 0xFFFFFFFF);
                    if (!node.label.isEmpty()) guiGraphics.drawCenteredString(this.font, node.label, nx, ny - 18, color);
                }

                if (selectedUpgradeNode != -1 && !suitNodes[selectedUpgradeNode].purchased && nodesOwned > 0) {
                    this.applyNodeButton.active = true;
                } else {
                    this.applyNodeButton.active = false;
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Click Main Tabs (Left Edge)
        for (int m = 0; m < 2; m++) {
            int tabX = x - 28;
            int tabY = y + 20 + (m * 35);
            if (mouseX >= tabX && mouseX <= tabX + 28 && mouseY >= tabY && mouseY <= tabY + 30) {
                mainMode = m; 
                ModMessages.sendToServer(new SyncWorkbenchModePacket(this.menu.blockEntity.getBlockPos(), mainMode));
                return true;
            }
        }

        // Click Sub Tabs (Right Edge)
        if (mainMode == 1) {
            for (int s = 0; s < 2; s++) {
                int subTabX = x + imageWidth;
                int subTabY = y + 20 + (s * 35);
                if (mouseX >= subTabX && mouseX <= subTabX + 28 && mouseY >= subTabY && mouseY <= subTabY + 30) {
                    subMode = s;
                    return true;
                }
            }
        }

        if (mainMode == 0) {
            for (int i = 0; i < Math.min(MAX_VISIBLE_TABS, tabIcons.length - tabStartIndex); i++) {
                int t = tabStartIndex + i;
                int tabX = x + 200 + (i * 24);
                int tabY = y + 6;
                if (mouseX >= tabX && mouseX <= tabX + 24 && mouseY >= tabY && mouseY <= tabY + 24) {
                    selectedTab = t;     
                    selectedIndex = 0;   
                    return true;
                }
            }

            if (tabStartIndex > 0 && mouseX >= x + 184 && mouseX <= x + 198 && mouseY >= y + 6 && mouseY <= y + 30) {
                tabStartIndex--;
                return true;
            }
            if (tabStartIndex < tabIcons.length - MAX_VISIBLE_TABS && mouseX >= x + 296 && mouseX <= x + 310 && mouseY >= y + 6 && mouseY <= y + 30) {
                tabStartIndex++;
                return true;
            }

            if (mouseX >= x + 184 && mouseX <= x + 310) {
                ItemStack[] currentList = categorizedBlueprints[selectedTab];
                for (int i = 0; i < currentList.length; i++) {
                    int itemY = y + 42 + (i * 26);
                    if (mouseY >= itemY - 2 && mouseY <= itemY + 22) {
                        selectedIndex = i;
                        return true;
                    }
                }
            }
        } 
        else if (mainMode == 1) {
            if (subMode == 0) {
                int listX = x + 250;
                int listY = y + 35;
                for (int i = 0; i < 5; i++) {
                    int batteryIndex = batteryStartIndex + i;
                    if (batteryIndex < batteryList.length) {
                        int rowY = listY + (i * 40);
                        if (mouseX >= listX && mouseX <= listX + 165 && mouseY >= rowY && mouseY <= rowY + 35) {
                            selectedBatteryIndex = batteryIndex; 
                            return true;
                        }
                    }
                }
            } 
            else if (subMode == 1) {
                int treeX = x + 135;
                int treeY = y + 7;
                for (UpgradeNode node : suitNodes) {
                    int nx = treeX + node.x;
                    int ny = treeY + node.y;
                    if (mouseX >= nx - 10 && mouseX <= nx + 10 && mouseY >= ny - 10 && mouseY <= ny + 10) {
                        selectedUpgradeNode = node.id;
                        Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 0.5F, 1.5F);
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