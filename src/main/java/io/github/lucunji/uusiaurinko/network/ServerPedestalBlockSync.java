package io.github.lucunji.uusiaurinko.network;

import io.github.lucunji.uusiaurinko.tileentity.PedestalTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author DustW
 */
public class ServerPedestalBlockSync implements IServerMessage {
    BlockPos blockPos;
    CompoundNBT nbt;

    public ServerPedestalBlockSync(BlockPos blockPos, CompoundNBT nbt) {
        this.blockPos = blockPos;
        this.nbt = nbt;
    }

    public static void encode(ServerPedestalBlockSync msg, PacketBuffer buffer) {
        buffer.writeBlockPos(msg.blockPos);
        buffer.writeCompoundTag(msg.nbt);
    }

    public static ServerPedestalBlockSync decode(PacketBuffer buffer) {
        return new ServerPedestalBlockSync(buffer.readBlockPos(), buffer.readCompoundTag());
    }

    public static void handle(ServerPedestalBlockSync msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) {
            ctx.get().enqueueWork(() -> {
                Minecraft.getInstance().world.getTileEntity(msg.blockPos).deserializeNBT(msg.nbt);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
