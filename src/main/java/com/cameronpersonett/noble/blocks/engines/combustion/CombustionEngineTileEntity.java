package com.cameronpersonett.noble.blocks.engines.combustion;

import com.cameronpersonett.noble.blocks.engines.AbstractEngineTileEntity;
import com.cameronpersonett.noble.core.Config;
import com.cameronpersonett.noble.core.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CombustionEngineTileEntity extends AbstractEngineTileEntity {
    protected ItemStackHandler inventory = createHandler();
    protected LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    public CombustionEngineTileEntity() {
        super(Registration.COMBUSTION_ENGINE_TILE_ENTITY.get());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryHandler.invalidate();
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            return;
        }

        if (counter > 0) {
            counter--;
            if (counter <= 0) {
                energyStorage.addEnergy(Config.COMBUSTION_ENGINE_GENERATION);
            } setChanged();
        }

        if (counter <= 0) {
            ItemStack stack = inventory.getStackInSlot(0);
            if (stack.getItem() == Items.DIAMOND) {
                inventory.extractItem(0, 1, false);
                counter = Config.COMBUSTION_ENGINE_TICKS;
                setChanged();
            }
        }

        BlockState blockState = level.getBlockState(getBlockPos());
        if (blockState.getValue(BlockStateProperties.POWERED) != counter > 0) {
            level.setBlock(getBlockPos(), blockState.setValue(BlockStateProperties.POWERED, counter > 0),
                    Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
        } sendOutPower();
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
                return stack.getItem() == Items.DIAMOND;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (stack.getItem() != Items.DIAMOND) {
                    return stack;
                } return super.insertItem(slot, stack, simulate);
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
