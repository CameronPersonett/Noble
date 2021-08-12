package com.cameronpersonett.noble.blocks.engines;

import com.cameronpersonett.noble.tools.CustomEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Objects;

public abstract class AbstractEngineContainer extends Container {
    public final AbstractEngineTileEntity entity;

    public AbstractEngineContainer(final int windowID, final PlayerInventory inv, final AbstractEngineTileEntity entity, ContainerType type) {
        super(type, windowID);
        this.entity = entity;

        int xStart = 7;
        int yStart = 82;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                int x = xStart + (j * 16) + (j * 2);
                int y = yStart + (i * 16) + (i * 2);
                addSlot(new Slot(inv, j + i * 9 + 9, x, y));
            }
        }

        yStart = 140;
        for(int j = 0; j < 9; j++) {
            int x = xStart + (j * 16) + (j * 2);
            addSlot(new Slot(inv, j, x, yStart));
        } trackPower();
    }

    // Setup syncing of power from server to client so that the GUI can show the amount of power in the block
    private void trackPower() {
        // Unfortunatelly on a dedicated server ints are actually truncated to short so we need
        // to split our integer here (split our 32 bit integer into two 16 bit integers)
        addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return getEnergy() & 0xffff;
            }

            @Override
            public void set(int value) {
                entity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0xffff0000;
                    ((CustomEnergyStorage)h).setEnergy(energyStored + (value & 0xffff));
                });
            }
        });
        addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                entity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    ((CustomEnergyStorage)h).setEnergy(energyStored | (value << 16));
                });
            }
        });
    }

    protected static AbstractEngineTileEntity getTileEntity(final PlayerInventory inv, final PacketBuffer data) {
        Objects.requireNonNull(inv, "Player inventory cannot be null.");
        Objects.requireNonNull(data, "Packet Buffer cannot be null.");
        TileEntity entity = inv.player.level.getBlockEntity(data.readBlockPos());
        if(entity instanceof AbstractEngineTileEntity) {
            return (AbstractEngineTileEntity)entity;
        } throw new IllegalStateException("Tile entity is incorrect.");
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.blockPosition().closerThan(entity.getBlockPos(), 5f);
    }

    @Override
    public abstract ItemStack quickMoveStack(PlayerEntity player, int index);

    public int getEnergy() {
        return entity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
}