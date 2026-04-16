package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rigmod.item.ModItems;
import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.menu.RigWorkbenchMenu;
import com.rigmod.network.ModMessages;
import com.rigmod.network.packet.CraftArmorPacket;
import com.rigmod.network.packet.RechargeArmorPacket;
import com.rigmod.network.packet.ApplyNodePacket;
import com.rigmod.network.packet.SyncWorkbenchModePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class RigWorkbenchScreen extends AbstractContainerScreen<RigWorkbenchMenu> {

    private int mainMode = 0; 
    private int subMode = 0; 
    private int treeMode = 0; // 0 = Suit Selection List, 1 = Node Tree
    
    private Button craftButton;
    private Button rechargeButton;
    private Button applyNodeButton; 
    private Button upgradeSuitButton; 
    private Button backButton;        
    private Button btnScrollLeft;  
    private Button btnScrollRight; 

    private int selectedTab = 0;   
    private int selectedIndex = 0; 
    private int tabStartIndex = 0; 
    private final int MAX_VISIBLE_TABS = 4; 

    private float batteryScrollOffset = 0.0f;
    private int batteryStartIndex = 0;
    private int selectedBatteryIndex = -1; 
    
    private static class SuitEntry {
        ItemStack stack;
        int slot;
        public SuitEntry(ItemStack stack, int slot) { this.stack = stack; this.slot = slot; }
    }
    private List<SuitEntry> availableSuits = new ArrayList<>();
    private int selectedSuitListIndex = 0;

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
    private UpgradeNode[] currentSuitNodes = new UpgradeNode[0]; 

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
        new ItemStack[]{ 
            new ItemStack(ModItems.ENGINEERING_LEVEL_3_HELMET.get()),
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get())
        },
        new ItemStack[]{ 
            new ItemStack(ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get()), 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get())
        },
        new ItemStack[]{ 
            new ItemStack(ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get()), 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get()),
            new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get())
        },
        new ItemStack[]{ 
            new ItemStack(ModItems.ENGINEERING_LEVEL_2_BOOTS.get()), 
            new ItemStack(ModItems.STANDARD_LEVEL_1_BOOTS.get())
        },
        new ItemStack[]{ 
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

    private void refreshAvailableSuits() {
        availableSuits.clear();
        Player player = Minecraft.getInstance().player;
        
        ItemStack equippedChest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (equippedChest.getItem() instanceof Custom3DArmorItem armor && armor.getType() == ArmorItem.Type.CHESTPLATE) {
            availableSuits.add(new SuitEntry(equippedChest, -100));
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof Custom3DArmorItem armor && armor.getType() == ArmorItem.Type.CHESTPLATE) {
                availableSuits.add(new SuitEntry(stack, i));
            }
        }
        
        if (selectedSuitListIndex >= availableSuits.size()) selectedSuitListIndex = 0;
    }

    private void loadSuitNodes() {
        if (availableSuits.isEmpty() || selectedSuitListIndex >= availableSuits.size()) return;
        SuitEntry entry = availableSuits.get(selectedSuitListIndex);
        Custom3DArmorItem customArmor = (Custom3DArmorItem) entry.stack.getItem();
        int armorLvl = customArmor.getArmorLevel();
        
        if (armorLvl == 1) {
            currentSuitNodes = new UpgradeNode[]{
                new UpgradeNode(0, 15, 65, "RIG", true, 1),
                new UpgradeNode(1, 45, 65, "HP", false, 4),
                new UpgradeNode(4, 75, 65, "", false, 5),
                new UpgradeNode(5, 105, 65, "PWR", false)
            };
        } else if (armorLvl == 2) {
            currentSuitNodes = new UpgradeNode[]{
                new UpgradeNode(0, 15, 65, "RIG", true, 1),
                new UpgradeNode(1, 45, 65, "HP", false, 2, 4),
                new UpgradeNode(2, 75, 65, "", false, 3),
                new UpgradeNode(3, 105, 65, "ARM", false),
                new UpgradeNode(4, 45, 45, "", false, 5),
                new UpgradeNode(5, 75, 45, "PWR", false, 6),
                new UpgradeNode(6, 105, 45, "SPD", false)
            };
        } else if (armorLvl >= 3) {
            currentSuitNodes = new UpgradeNode[]{
                new UpgradeNode(0, 15, 65, "RIG", true, 1),
                new UpgradeNode(1, 45, 65, "HP", false, 2, 4, 7),
                new UpgradeNode(2, 75, 65, "", false, 3),
                new UpgradeNode(3, 105, 65, "ARM", false, 10),
                new UpgradeNode(4, 45, 45, "", false, 5),
                new UpgradeNode(5, 75, 45, "PWR", false, 6),
                new UpgradeNode(6, 105, 45, "SPD", false),
                new UpgradeNode(7, 45, 85, "HP", false, 8),
                new UpgradeNode(8, 75, 85, "", false, 9),
                new UpgradeNode(9, 105, 85, "SP1", false),
                new UpgradeNode(10, 135, 65, "", false, 11),
                new UpgradeNode(11, 165, 65, "CAP", false, 12),
                new UpgradeNode(12, 195, 65, "ARM", false)
            };
        }

        CompoundTag tag = entry.stack.getOrCreateTag();
        for (UpgradeNode node : currentSuitNodes) {
            if (node.id != 0) {
                node.purchased = tag.getBoolean("RigNode_" + node.id);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        refreshAvailableSuits();

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

        this.upgradeSuitButton = addRenderableWidget(Button.builder(Component.literal("Upgrade"), button -> {
            if (!availableSuits.isEmpty()) {
                loadSuitNodes();
                treeMode = 1; 
                selectedUpgradeNode = -1;
                Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.2F);
            }
            this.setFocused(null);
        }).bounds(x + 329, y + 225, 90, 20).build()); 

        this.backButton = addRenderableWidget(Button.builder(Component.literal("Back"), button -> {
            treeMode = 0;
            refreshAvailableSuits(); 
            Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 0.8F);
            this.setFocused(null);
        }).bounds(x + 140, y + 225, 60, 20).build()); 

        this.applyNodeButton = addRenderableWidget(Button.builder(Component.literal("Apply Node"), button -> {
            UpgradeNode targetNode = null;
            for (UpgradeNode n : currentSuitNodes) {
                if (n.id == selectedUpgradeNode) { targetNode = n; break; }
            }
            if (targetNode != null && !targetNode.purchased && isNodeReachable(selectedUpgradeNode) && !availableSuits.isEmpty()) {
                targetNode.purchased = true; 
                availableSuits.get(selectedSuitListIndex).stack.getOrCreateTag().putBoolean("RigNode_" + selectedUpgradeNode, true); 
                
                int targetSlot = availableSuits.get(selectedSuitListIndex).slot;
                ModMessages.sendToServer(new ApplyNodePacket(selectedUpgradeNode, targetSlot));
                
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
            if (stack.getItem() == item) count += stack.getCount();
        }
        return count;
    }

    private boolean hasNode(int id) {
        if (currentSuitNodes == null) return false;
        for (UpgradeNode n : currentSuitNodes) {
            if (n.id == id && n.purchased) return true;
        }
        return false;
    }

    private boolean isNodeReachable(int targetId) {
        if (targetId == 0) return true; 
        for (UpgradeNode node : currentSuitNodes) {
            if (node.purchased) {
                for (int link : node.links) {
                    if (link == targetId) return true;
                }
            }
        }
        return false;
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
        this.applyNodeButton.visible = (mainMode == 1 && subMode == 1 && treeMode == 1);
        this.upgradeSuitButton.visible = (mainMode == 1 && subMode == 1 && treeMode == 0);
        this.backButton.visible = (mainMode == 1 && subMode == 1 && treeMode == 1);
        
        this.btnScrollLeft.visible = (mainMode == 0);
        this.btnScrollRight.visible = (mainMode == 0);
        this.btnScrollLeft.active = (tabStartIndex > 0);
        this.btnScrollRight.active = (tabStartIndex < tabIcons.length - MAX_VISIBLE_TABS);

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

        if (mainMode == 1) {
            for (int s = 0; s < 2; s++) {
                int subTabX = x + imageWidth; 
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
            
            if (subMode == 0) {
                guiGraphics.fill(x + 6, y + 6, x + 210, y + imageHeight - 6, 0xFF333333);
                guiGraphics.fill(x + 7, y + 7, x + 209, y + imageHeight - 7, 0xFF181818);
                guiGraphics.fill(x + 242, y + 6, x + 434, y + imageHeight - 6, 0xFF333333);
                guiGraphics.fill(x + 243, y + 7, x + 433, y + imageHeight - 7, 0xFF181818);

                guiGraphics.drawCenteredString(this.font, "Power Injection Matrix", x + 108, y + 15, 0xFF00E5FF);
                ItemStack equippedChest = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.CHEST);
                
                if (equippedChest.getItem() instanceof Custom3DArmorItem) {
                    PoseStack pose = guiGraphics.pose();
                    pose.pushPose();
                    pose.translate(x + 108, y + 95, 100); 
                    pose.scale(4.5F, 4.5F, 4.5F); 
                    guiGraphics.renderItem(equippedChest, -8, -8);
                    pose.popPose();

                    CompoundTag tag = equippedChest.getOrCreateTag();
                    int power = tag.getInt("RigPower");
                    int maxPower = 100 + (tag.getBoolean("RigNode_5") ? 50 : 0) + (tag.getBoolean("RigNode_11") ? 50 : 0);
                    
                    boolean playerOwnsSelectedBattery = false;
                    if (selectedBatteryIndex != -1) {
                        playerOwnsSelectedBattery = countItem(batteryList[selectedBatteryIndex].getItem()) > 0;
                    }
                    this.rechargeButton.active = (power < maxPower && playerOwnsSelectedBattery);
                    
                    int powerColor = (power >= maxPower) ? 0xFF55FFFF : (power > maxPower/2) ? 0xFF55FF55 : (power > maxPower/5) ? 0xFFFFFF55 : 0xFFFF5555;

                    guiGraphics.drawCenteredString(this.font, equippedChest.getHoverName().getString(), x + 108, y + 160, 0xFFFFFF);
                    guiGraphics.drawCenteredString(this.font, "Core Power: " + power + " / " + maxPower, x + 108, y + 175, powerColor);
                    
                    int barWidth = 140;
                    int barHeight = 8;
                    int barX = x + 108 - (barWidth / 2); 
                    int barY = y + 190;

                    guiGraphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);
                    guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
                    
                    if (power > 0) {
                        int fillWidth = (int) (((float)power / maxPower) * barWidth);
                        guiGraphics.fill(barX, barY, barX + Math.min(fillWidth, barWidth), barY + barHeight, powerColor);
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
                        if (batteryIndex == selectedBatteryIndex) guiGraphics.fill(listX - 1, rowY - 1, listX + 166, rowY + 36, 0xFF00FFFF);
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
            // ==========================================
            // 🔥 SUB-MODE 1: UPGRADE (COMPACT DESIGN)
            // ==========================================
            else if (subMode == 1) {
                guiGraphics.fill(x + 6, y + 6, x + 434, y + 254, 0xFF333333);
                guiGraphics.fill(x + 7, y + 7, x + 433, y + 253, 0xFF0D1117); 

                int nodesOwned = countItem(ModItems.UPGRADE_NODE.get());

                guiGraphics.fill(x + 15, y + 15, x + 125, y + 75, 0xFF4A4A4A);
                guiGraphics.fill(x + 17, y + 17, x + 123, y + 73, 0xFF222222);
                guiGraphics.drawCenteredString(this.font, "NODES", x + 70, y + 22, 0xFF00E5FF);
                
                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.translate(x + 70, y + 48, 0); 
                pose.scale(1.5F, 1.5F, 1.5F); 
                guiGraphics.renderItem(new ItemStack(ModItems.UPGRADE_NODE.get()), -8, -8);
                pose.popPose();
                
                guiGraphics.drawCenteredString(this.font, "x " + nodesOwned, x + 70, y + 60, 0xFFFFFF);

                if (treeMode == 0) {
                    guiGraphics.drawCenteredString(this.font, "UPGRADE BENCH", x + 284, y + 15, 0xFF00E5FF);

                    if (availableSuits.isEmpty()) {
                        guiGraphics.drawCenteredString(this.font, "No Upgradable Suits Found.", x + 284, y + 80, 0xFFFF5555);
                        this.upgradeSuitButton.active = false;
                    } else {
                        this.upgradeSuitButton.active = true;
                        guiGraphics.fill(x + 140, y + 30, x + 425, y + imageHeight - 40, 0xFF4A4A4A);
                        guiGraphics.fill(x + 142, y + 32, x + 423, y + imageHeight - 42, 0xFF181818);

                        for (int i = 0; i < Math.min(6, availableSuits.size()); i++) {
                            SuitEntry entry = availableSuits.get(i);
                            int rowY = y + 35 + (i * 26);
                            boolean isSelected = (selectedSuitListIndex == i);
                            
                            guiGraphics.fill(x + 145, rowY, x + 420, rowY + 22, isSelected ? 0xFF00E5FF : 0xFF4A4A4A);
                            guiGraphics.fill(x + 146, rowY + 1, x + 419, rowY + 21, 0xFF222222);
                            guiGraphics.renderItem(entry.stack, x + 150, rowY + 3);
                            
                            String name = entry.stack.getHoverName().getString();
                            if (name.length() > 24) name = name.substring(0, 24) + "...";
                            guiGraphics.drawString(this.font, name, x + 175, rowY + 7, isSelected ? 0xFF00E5FF : 0xFFFFFF, false);
                            
                            if (entry.slot == -100) guiGraphics.drawString(this.font, "(Equipped)", x + 350, rowY + 7, 0xFFAAAAAA, false);
                        }

                        SuitEntry selectedEntry = availableSuits.get(selectedSuitListIndex);
                        CompoundTag tag = selectedEntry.stack.getOrCreateTag();
                        int currentHP = 100 + (tag.getBoolean("RigNode_1") ? 50 : 0) + (tag.getBoolean("RigNode_7") ? 50 : 0);
                        int currentArm = 25 + (tag.getBoolean("RigNode_3") ? 15 : 0) + (tag.getBoolean("RigNode_12") ? 15 : 0);
                        int currentPwr = 100 + (tag.getBoolean("RigNode_5") ? 50 : 0) + (tag.getBoolean("RigNode_11") ? 50 : 0);

                        guiGraphics.fill(x + 15, y + 80, x + 125, y + 220, 0xFF4A4A4A);
                        guiGraphics.fill(x + 17, y + 82, x + 123, y + 218, 0xFF222222);
                        guiGraphics.drawString(this.font, "SPECIFICATIONS", x + 25, y + 90, 0xFF00E5FF, false);
                        guiGraphics.drawString(this.font, "HP:  " + currentHP, x + 25, y + 110, tag.getBoolean("RigNode_1") || tag.getBoolean("RigNode_7") ? 0xFF55FF55 : 0xFFAAAAAA, false);
                        guiGraphics.drawString(this.font, "ARM: " + currentArm, x + 25, y + 125, tag.getBoolean("RigNode_3") || tag.getBoolean("RigNode_12") ? 0xFF55FF55 : 0xFFAAAAAA, false);
                        guiGraphics.drawString(this.font, "PWR: " + currentPwr, x + 25, y + 140, tag.getBoolean("RigNode_5") || tag.getBoolean("RigNode_11") ? 0xFF55FF55 : 0xFFAAAAAA, false);
                    }
                } 
                else if (treeMode == 1) {
                    
                    int currentHP = 100 + (hasNode(1) ? 50 : 0) + (hasNode(7) ? 50 : 0);
                    int currentArm = 25 + (hasNode(3) ? 15 : 0) + (hasNode(12) ? 15 : 0);
                    int currentPwr = 100 + (hasNode(5) ? 50 : 0) + (hasNode(11) ? 50 : 0);

                    guiGraphics.fill(x + 15, y + 80, x + 125, y + 220, 0xFF4A4A4A);
                    guiGraphics.fill(x + 17, y + 82, x + 123, y + 218, 0xFF222222);
                    guiGraphics.drawString(this.font, "SPECIFICATIONS", x + 25, y + 90, 0xFF00E5FF, false);
                    guiGraphics.drawString(this.font, "HP:  " + currentHP, x + 25, y + 110, hasNode(1) || hasNode(7) ? 0xFF55FF55 : 0xFFAAAAAA, false);
                    guiGraphics.drawString(this.font, "ARM: " + currentArm, x + 25, y + 125, hasNode(3) || hasNode(12) ? 0xFF55FF55 : 0xFFAAAAAA, false);
                    guiGraphics.drawString(this.font, "PWR: " + currentPwr, x + 25, y + 140, hasNode(5) || hasNode(11) ? 0xFF55FF55 : 0xFFAAAAAA, false);

                    // 🔥 NEW BASE COORDINATES FOR CENTERED TREE
                    int treeX = x + 180; 
                    int treeY = y + 60;

                    for (UpgradeNode node : currentSuitNodes) {
                        for (int targetId : node.links) {
                            UpgradeNode target = null;
                            for (UpgradeNode n : currentSuitNodes) if (n.id == targetId) target = n;
                            if (target == null) continue;

                            int startX = treeX + node.x;
                            int startY = treeY + node.y;
                            int endX = treeX + target.x;
                            int endY = treeY + target.y;
                            
                            int lineColor = (node.purchased && target.purchased) ? 0xFF00E5FF : 0xFF2A3A4A;
                            guiGraphics.fill(Math.min(startX, endX) - 1, Math.min(startY, endY) - 1, 
                                             Math.max(startX, endX) + 1, Math.max(startY, endY) + 1, lineColor);
                        }
                    }
                    
                    for (UpgradeNode node : currentSuitNodes) {
                        int nx = treeX + node.x;
                        int ny = treeY + node.y;
                        int color = node.purchased ? 0xFF00E5FF : 0xFF556677;
                        
                        if (selectedUpgradeNode == node.id) guiGraphics.fill(nx - 8, ny - 8, nx + 8, ny + 8, 0x88FFFFFF);
                        guiGraphics.fill(nx - 6, ny - 6, nx + 6, ny + 6, 0xFF0D1117);
                        guiGraphics.fill(nx - 4, ny - 4, nx + 4, ny + 4, color);
                        if (node.purchased) guiGraphics.fill(nx - 1, ny - 1, nx + 1, ny + 1, 0xFFFFFFFF);
                        
                        if (!node.label.isEmpty()) {
                            guiGraphics.pose().pushPose();
                            guiGraphics.pose().translate(nx, ny - 15, 0);
                            guiGraphics.pose().scale(0.8F, 0.8F, 1.0F); 
                            guiGraphics.drawCenteredString(this.font, node.label, 0, 0, color);
                            guiGraphics.pose().popPose();
                        }
                    }

                    UpgradeNode targetNode = null;
                    for (UpgradeNode n : currentSuitNodes) if (n.id == selectedUpgradeNode) targetNode = n;

                    if (targetNode != null && !targetNode.purchased && nodesOwned > 0 && isNodeReachable(selectedUpgradeNode)) {
                        this.applyNodeButton.active = true;
                    } else {
                        this.applyNodeButton.active = false;
                    }
                }
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
                Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.0F);
                return true;
            }
        }

        if (mainMode == 1) {
            for (int s = 0; s < 2; s++) {
                int subTabX = x + imageWidth;
                int subTabY = y + 20 + (s * 35);
                if (mouseX >= subTabX && mouseX <= subTabX + 28 && mouseY >= subTabY && mouseY <= subTabY + 30) {
                    subMode = s;
                    treeMode = 0; 
                    refreshAvailableSuits();
                    Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 1.0F, 1.0F);
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
                if (treeMode == 0) {
                    for (int i = 0; i < Math.min(6, availableSuits.size()); i++) {
                        int rowY = y + 35 + (i * 26);
                        if (mouseX >= x + 145 && mouseX <= x + 420 && mouseY >= rowY && mouseY <= rowY + 22) {
                            selectedSuitListIndex = i;
                            Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 0.8F, 1.2F);
                            return true;
                        }
                    }
                }
                else if (treeMode == 1) {
                    // 🔥 NEW BASE COORDINATES FOR CENTERED TREE HITBOXES
                    int treeX = x + 180;
                    int treeY = y + 60;
                    for (UpgradeNode node : currentSuitNodes) {
                        int nx = treeX + node.x;
                        int ny = treeY + node.y;
                        if (mouseX >= nx - 8 && mouseX <= nx + 8 && mouseY >= ny - 8 && mouseY <= ny + 8) {
                            selectedUpgradeNode = node.id;
                            Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.get(), 0.5F, 1.5F);
                            return true;
                        }
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