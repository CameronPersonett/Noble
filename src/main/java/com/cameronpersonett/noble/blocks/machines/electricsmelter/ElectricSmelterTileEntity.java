package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.blocks.machines.AbstractMachineTileEntity;
import com.cameronpersonett.noble.core.Registration;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;

public class ElectricSmelterTileEntity extends AbstractMachineTileEntity {
    protected ItemStackHandler inventory = createHandler();
    protected LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    public ElectricSmelterTileEntity() {
        super(Registration.ELECTRIC_SMELTER_TILE_ENTITY.get());
        this.progress = 0;
        this.energyUsagePerProgress = 10;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inventoryHandler.invalidate();
    }

    @Override
    public void tick() {
        if(!level.isClientSide) {
            if(inventory.getStackInSlot(0).isEmpty()) {
                progress = 0;
            } else {
                RecipeManager recipeManager = level.getRecipeManager();
                Inventory input = new Inventory(inventory.getStackInSlot(0));
                Optional<FurnaceRecipe> matchingRecipe = recipeManager.getRecipeFor(IRecipeType.SMELTING, input, level);
                ItemStack result = matchingRecipe.get().getResultItem();

                if(inventory.getStackInSlot(1).isEmpty() || inventory.getStackInSlot(1).sameItem(result)) {
                    if(inventory.getStackInSlot(1).getCount() < inventory.getStackInSlot(1).getMaxStackSize()) {
                        if(energyStorage.getEnergyStored() > energyUsagePerProgress) {
                            energyStorage.consumeEnergy(energyUsagePerProgress);
                            progress += 100;
                        }

                        if(progress == 100) {
                            progress = 0;

                            inventory.extractItem(0, 1, false);

                            if(inventory.getStackInSlot(1).isEmpty()) {
                                result.setCount(1);
                                inventory.insertItem(1, result, false);
                            } else {
                                inventory.getStackInSlot(1).grow(1);
                            }
                        }
                    }
                }
            }
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
        return new ItemStackHandler(2) {
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