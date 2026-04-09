package com.rigmod.network;

import com.rigmod.RigMod;
import com.rigmod.network.packet.CraftArmorPacket;
import com.rigmod.network.packet.CycleRadarModePacket;
import com.rigmod.network.packet.CycleVisionModePacket;
import com.rigmod.network.packet.SyncWorkbenchModePacket; // ✅ NEW: Import added!
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(RigMod.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(CycleVisionModePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CycleVisionModePacket::new)
                .encoder(CycleVisionModePacket::toBytes)
                .consumerMainThread(CycleVisionModePacket::handle)
                .add();

        net.messageBuilder(CycleRadarModePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CycleRadarModePacket::new)
                .encoder(CycleRadarModePacket::toBytes)
                .consumerMainThread(CycleRadarModePacket::handle)
                .add();

        net.messageBuilder(CraftArmorPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(CraftArmorPacket::new)
                .encoder(CraftArmorPacket::toBytes)
                .consumerMainThread(CraftArmorPacket::handle)
                .add();

        // THE FIX: The server now knows what the Hologram sync packet is!
        net.messageBuilder(SyncWorkbenchModePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SyncWorkbenchModePacket::new)
                .encoder(SyncWorkbenchModePacket::toBytes)
                .consumerMainThread(SyncWorkbenchModePacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}