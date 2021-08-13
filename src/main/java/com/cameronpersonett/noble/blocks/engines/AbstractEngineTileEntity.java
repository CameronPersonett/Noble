package com.cameronpersonett.noble.blocks.engines;

import com.cameronpersonett.noble.blocks.machines.AbstractMachineTileEntity;
import com.cameronpersonett.noble.core.Config;
import com.cameronpersonett.noble.tools.CustomEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractEngineTileEntity extends TileEntity implements ITickableTileEntity {
    protected int progress;
    protected CustomEnergyStorage energyStorage = createEnergy();

    protected LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    public AbstractEngineTileEntity(TileEntityType type) {
        super(type);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        energy.invalidate();
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.COMBUSTION_ENGINE_MAX_POWER, 0, 1000) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("energy", energyStorage.serializeNBT());
        return super.save(tag);
    }

    @Override
    public abstract void tick();

    protected void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                TileEntity te = level.getBlockEntity(getBlockPos().offset(new Vector3i(direction.getStepX(), direction.getStepY(), direction.getStepZ())));
                if(te instanceof AbstractMachineTileEntity) {
                    boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(energy -> {
                        if (energy.canReceive()) {
                            int received = energy.receiveEnergy(Math.min(capacity.get(), Config.COMBUSTION_ENGINE_SEND), false);
                            capacity.addAndGet(-received);
                            energyStorage.consumeEnergy(received);
                            setChanged();
                            return capacity.get() > 0;
                        } else {
                            return true;
                        }
                    }).orElse(true);

                    if (!doContinue) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        } return super.getCapability(cap, side);
    }
}
