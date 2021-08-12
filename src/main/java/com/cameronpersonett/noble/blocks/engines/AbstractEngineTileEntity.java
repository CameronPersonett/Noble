package com.cameronpersonett.noble.blocks.engines;

import com.cameronpersonett.noble.core.Config;
import com.cameronpersonett.noble.tools.CustomEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public abstract class AbstractEngineTileEntity extends TileEntity implements ITickableTileEntity {
    protected int counter;
    protected CustomEnergyStorage energyStorage = createEnergy();

    // Never create lazy optionals in getCapability. Always place them as fields in the tile entity:
    protected LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    public AbstractEngineTileEntity(TileEntityType type) {
        super(type);
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.COMBUSTION_ENGINE_MAX_POWER, 0) {
            @Override
            protected void onEnergyChanged() {
                // UNSURE ABOUT THIS ONE AS WELL, WAS markDirty()
                setChanged();
            }
        };
    }

    // called to load the TE onto the server from the hard drive
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
    }

    // called to save the TE to the hard drive from the server
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        return super.save(tag);
    }

    // called to generate NBT for a syncing packet when a client loads a chunk that this TE is in
    @Override
    public CompoundNBT getUpdateTag() {
        // we want to tell the client about as much data as it needs to know
        // since it doesn't know any data at this point, we can usually just defer to write() above
        // if you have data that would be written to the disk but the client doesn't ever need to know,
        // you can just sync the need-to-know data instead of calling write()
        // there's an equivalent method for reading the update tag but it just defaults to read() anyway
        return this.save(new CompoundNBT());
    }

    // we can sync a TileEntity from the server to all tracking clients by calling world.notifyBlockUpdate

    // when that happens, this method is called on the server to generate a packet to send to the client
    // if you have lots of data, it's a good idea to keep track of which data has changed since the last time
    // this TE was synced, and then only send the changed data;
    // this reduces the amount of packets sent, which is good
    // we only have one value to sync so we'll just write everything into the NBT again
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);

        // the number here is generally ignored for non-vanilla TileEntities, 0 is safest
        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, nbt);
    }

    // this method gets called on the client when it receives the packet that was sent in the previous method
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.load(level.getBlockEntity(packet.getPos()).getBlockState(), packet.getTag());
    }

    @Override
    public abstract void tick();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } return super.getCapability(cap, side);
    }
}
