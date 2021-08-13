package com.cameronpersonett.noble.blocks.engines.combustion;

import com.cameronpersonett.noble.blocks.engines.AbstractEngineContainer;
import com.cameronpersonett.noble.core.Registration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CombustionEngineContainer extends AbstractEngineContainer {
    public CombustionEngineContainer(final int windowID, final PlayerInventory inv, final CombustionEngineTileEntity entity) {
        super(windowID, inv, entity, Registration.COMBUSTION_ENGINE_CONTAINER.get());

        entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new SlotItemHandler(h, 0, 49, 33));
        });
    }

    public CombustionEngineContainer(final int windowID, final PlayerInventory inv, final PacketBuffer data) {
        this(windowID, inv, getTileEntity(inv, data));
    }

    protected static CombustionEngineTileEntity getTileEntity(final PlayerInventory inv, final PacketBuffer data) {
        TileEntity entity = AbstractEngineContainer.getTileEntity(inv, data);
        if(entity instanceof CombustionEngineTileEntity) {
            return (CombustionEngineTileEntity)entity;
        } throw new IllegalStateException("Tile entity is incorrect.");
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
//            if (index == 0) {
//                if (!this.moveItemStackTo(stack, 1, 37, true)) {
//                    return ItemStack.EMPTY;
//                } slot.onQuickCraft(stack, itemstack);
//            } else {
//                if (stack.getItem() == Items.DIAMOND) {
//                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                } else if (index < 28) {
//                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
//                    return ItemStack.EMPTY;
//                }
//            }

            // OLD - mergeItemStack() - stack, beginIndex, endIndex, reverseOrder
            // IF reverseOrder, endIndex GETS SUBTRACTED BY 1
            if (index == 36) {
                if (!this.moveItemStackTo(stack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                } slot.onQuickCraft(stack, itemstack);
            } else {
                if (stack.getItem() == Items.DIAMOND) {
                    if (!this.moveItemStackTo(stack, 36, 37, true)) {
                        if(!this.moveItemStackTo(stack, 27, 35, false)) {
                            if(!this.moveItemStackTo(stack, 0, 26, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                } else if (index < 27) {
                    if (!this.moveItemStackTo(stack, 27, 35, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 36 && !this.moveItemStackTo(stack, 0, 26, false)) {
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
}