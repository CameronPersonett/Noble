package com.cameronpersonett.noble.blocks.engines.combustion;

import com.cameronpersonett.noble.blocks.engines.AbstractEngineBlock;
import com.cameronpersonett.noble.net.NobleNet;
import com.cameronpersonett.noble.net.messages.TileEntityUpdateMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
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
import net.minecraftforge.fml.network.PacketDistributor;

public class CombustionEngineBlock extends AbstractEngineBlock {
    public CombustionEngineBlock() {
        super("combustion_engine");
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CombustionEngineTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof CombustionEngineTileEntity) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.noble.combustion_engine");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new CombustionEngineContainer(i, playerInventory, (CombustionEngineTileEntity)tileEntity);
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
