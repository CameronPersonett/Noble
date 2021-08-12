package com.cameronpersonett.noble.net.messages;

import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MachineEntityUpdateMessage {
    BlockPos pos;
    CompoundNBT nbt;

    public MachineEntityUpdateMessage(BlockPos pos, CompoundNBT nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    public static void encode(MachineEntityUpdateMessage message, PacketBuffer buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeNbt(message.nbt);
    }

    public static MachineEntityUpdateMessage decode(PacketBuffer buffer) {
        return new MachineEntityUpdateMessage(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(MachineEntityUpdateMessage message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            TileEntity entity = Minecraft.getInstance().level.getBlockEntity(message.pos);
            CombustionEngineTileEntity cete = (CombustionEngineTileEntity)entity;
            cete.load(cete.getBlockState(), message.nbt);
        }); context.setPacketHandled(true);
    }
}
