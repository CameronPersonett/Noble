package com.cameronpersonett.noble.blocks.engines.combustion;

import com.cameronpersonett.noble.blocks.engines.AbstractEngineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CombustionEngineBlock extends AbstractEngineBlock {

    public CombustionEngineBlock() {
        super("combustion_engine");
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
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
                }; NetworkHooks.openGui((ServerPlayerEntity)player, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        } return ActionResultType.SUCCESS;
    }
}
