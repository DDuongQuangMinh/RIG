package com.rigmod.network.packet;

import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncWorkbenchModePacket {
    private final BlockPos pos;
    private final int mode;

    public SyncWorkbenchModePacket(BlockPos pos, int mode) {
        this.pos = pos;
        this.mode = mode;
    }

    public SyncWorkbenchModePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.mode = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(mode);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            // Updates the block entity on the Server, which then syncs to all other players!
            BlockEntity be = player.level().getBlockEntity(pos);
            if (be instanceof RigWorkbenchBlockEntity workbench) {
                workbench.setDisplayMode(mode);
            }
        });
        return true;
    }
}