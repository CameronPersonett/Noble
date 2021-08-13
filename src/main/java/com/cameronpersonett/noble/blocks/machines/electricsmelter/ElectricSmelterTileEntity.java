package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.blocks.machines.AbstractMachineTileEntity;
import com.cameronpersonett.noble.core.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ElectricSmelterTileEntity extends AbstractMachineTileEntity {
    protected ItemStackHandler inventory = createHandler();
    protected LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    public ElectricSmelterTileEntity() {
        super(Registration.ELECTRIC_SMELTER_TILE_ENTITY.get());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryHandler.invalidate();
    }

    @Override
    public void tick() {
        if(!level.isClientSide) {

        }
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        inventory.deserializeNBT(tag.getCompound("inv"));
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("inv", inventory.serializeNBT());
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

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return true;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        } return super.getCapability(cap, side);
    }
}