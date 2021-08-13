package com.cameronpersonett.noble.net.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TileEntityUpdateMessage {
    BlockPos pos;
    CompoundNBT nbt;

    public TileEntityUpdateMessage(BlockPos pos, CompoundNBT nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    public static void encode(TileEntityUpdateMessage message, PacketBuffer buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeNbt(message.nbt);
    }

    public static TileEntityUpdateMessage decode(PacketBuffer buffer) {
        return new TileEntityUpdateMessage(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(TileEntityUpdateMessage message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            TileEntity entity = Minecraft.getInstance().level.getBlockEntity(message.pos);
            entity.load(entity.getBlockState(), message.nbt);
        }); context.setPacketHandled(true);
    }
}
