package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineTileEntity;
import com.cameronpersonett.noble.core.Registration;
import com.cameronpersonett.noble.tools.CustomEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ElectricSmelterContainer extends Container {
        public final ElectricSmelterTileEntity entity;
        private final IWorldPosCallable canInteractWithCallable;

        public ElectricSmelterContainer(final int windowID, final PlayerInventory inv, final ElectricSmelterTileEntity entity) {
                super(Registration.ELECTRIC_SMELTER_CONTAINER.get(), windowID);
                this.entity = entity;
                this.canInteractWithCallable = IWorldPosCallable.create(this.entity.getLevel(), this.entity.getBlockPos());

                this.entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                        addSlot(new SlotItemHandler(h, 0, 49, 33));
                });

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
                }
        }

        public ElectricSmelterContainer(final int windowID, final PlayerInventory inv, final PacketBuffer data) {
                this(windowID, inv, getTileEntity(inv, data));
        }

        private static ElectricSmelterTileEntity getTileEntity(final PlayerInventory inv, final PacketBuffer data) {
                Objects.requireNonNull(inv, "Player inventory cannot be null.");
                Objects.requireNonNull(data, "Packet Buffer cannot be null.");
                final TileEntity entity = inv.player.level.getBlockEntity(data.readBlockPos());
                if(entity instanceof ElectricSmelterTileEntity) {
                        return (ElectricSmelterTileEntity)entity;
                } throw new IllegalStateException("Tile entity is incorrect.");
        }

        @Override
        public boolean stillValid(PlayerEntity player) {
                return player.blockPosition().closerThan(entity.getBlockPos(), 5f);
        }

        @Override
        @Nonnull
        public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
                ItemStack itemstack = ItemStack.EMPTY;
                Slot slot = this.slots.get(index);
                if (slot != null && slot.hasItem()) {
                        ItemStack stack = slot.getItem();
                        itemstack = stack.copy();
                        if (index == 0) {
                                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                                        return ItemStack.EMPTY;
                                }
                                slot.onQuickCraft(stack, itemstack);
                        } else {
                                if (stack.getItem() == Items.DIAMOND) {
                                        if (!this.moveItemStackTo(stack, 0, 1, false)) {
                                                return ItemStack.EMPTY;
                                        }
                                } else if (index < 28) {
                                        if (!this.moveItemStackTo(stack, 28, 37, false)) {
                                                return ItemStack.EMPTY;
                                        }
                                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
                                        return ItemStack.EMPTY;
                                }
                        }

                        if (stack.isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                        } else {
                                slot.setChanged();
                        }

                        if (stack.getCount() == itemstack.getCount()) {
                                return ItemStack.EMPTY;
                        } slot.onTake(player, stack);
                } return itemstack;
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

        public int getEnergy() {
                return entity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
        }
}
