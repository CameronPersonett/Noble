package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.blocks.machines.AbstractMachineBlock;
import com.cameronpersonett.noble.net.NobleNet;
import com.cameronpersonett.noble.net.messages.TileEntityUpdateMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;

public class ElectricSmelterBlock extends AbstractMachineBlock {
    public ElectricSmelterBlock() {
        super("electric_smelter");
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElectricSmelterTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ElectricSmelterTileEntity) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.noble.electric_smelter");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new ElectricSmelterContainer(i, playerInventory, (ElectricSmelterTileEntity)tileEntity);
                    }
                };

                NobleNet.CHANNEL.sendTo(new TileEntityUpdateMessage(pos, tileEntity.save(new CompoundNBT())),
                        ((ServerPlayerEntity)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                NetworkHooks.openGui((ServerPlayerEntity)player, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        } return ActionResultType.SUCCESS;
    }
}
