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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class CombustionEngineTileEntity extends AbstractEngineTileEntity {
    protected ItemStackHandler itemHandler = createHandler();
    protected LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public CombustionEngineTileEntity() {
        super(Registration.COMBUSTION_ENGINE_TILE_ENTITY.get());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
        energy.invalidate();
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
            ItemStack stack = itemHandler.getStackInSlot(0);
            if (stack.getItem() == Items.DIAMOND) {
                itemHandler.extractItem(0, 1, false);
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

    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                // UNSURE ABOUT THIS NEXT ONE, WAS .offset(direction)
                TileEntity te = level.getBlockEntity(getBlockPos().offset(new Vector3i(direction.getStepX(), direction.getStepY(), direction.getStepZ())));
                if (te != null) {
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
    public void load(BlockState state, CompoundNBT tag) {
        //itemHandler.deserializeNBT(tag.getCompound("inv"));
        //energyStorage.deserializeNBT(tag.getCompound("energy"));
        CompoundNBT inv = tag.getCompound("inven");
        CompoundNBT item = inv.getCompound("item");
        itemHandler.setStackInSlot(0, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item.getString("name"))), item.getInt("qty")));
        energyStorage.setEnergy(tag.getInt("eng"));
        counter = tag.getInt("counter");
        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        //tag.put("inv", itemHandler.serializeNBT());
        CompoundNBT item = new CompoundNBT();
        item.putString("name", itemHandler.getStackInSlot(0).getDisplayName().getString());
        item.putInt("qty", itemHandler.getStackInSlot(0).getCount());
        CompoundNBT inv = new CompoundNBT();
        inv.put("item", item);
        tag.put("inven", inv);
        //tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("eng", energyStorage.getEnergyStored());
        tag.putInt("counter", counter);
        return super.save(tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == Items.DIAMOND;
            }

            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (stack.getItem() != Items.DIAMOND) {
                    return stack;
                } return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        } return super.getCapability(cap, side);
    }
}
