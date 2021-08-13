package com.cameronpersonett.noble.blocks.machines;

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

public abstract class AbstractMachineTileEntity extends TileEntity implements ITickableTileEntity {
    protected int counter;
    protected CustomEnergyStorage energyStorage = createEnergy();
    protected LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    public AbstractMachineTileEntity(TileEntityType type) {
        super(type);
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.COMBUSTION_ENGINE_MAX_POWER, 1000) {
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
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        super.load(state, tag);
    }

    // called to save the TE to the hard drive from the server
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("energy", energyStorage.serializeNBT());
        return super.save(tag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);

        return new SUpdateTileEntityPacket(this.getBlockPos(), 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.load(level.getBlockEntity(packet.getPos()).getBlockState(), packet.getTag());
    }

    @Override
    public abstract void tick();

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } return super.getCapability(cap, side);
    }
}
