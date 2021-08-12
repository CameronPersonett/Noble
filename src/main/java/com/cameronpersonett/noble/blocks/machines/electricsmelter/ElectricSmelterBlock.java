package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineContainer;
import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineTileEntity;
import com.cameronpersonett.noble.core.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
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

import javax.annotation.Nullable;
import java.util.List;

public class ElectricSmelterBlock extends Block {
    public ElectricSmelterBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.0f));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag flags) {
        list.add(new TranslationTextComponent("message.electric_smelter"));
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return state.getValue(BlockStateProperties.POWERED) ? super.getLightValue(state, world, pos) : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ElectricSmelterTileEntity();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ElectricSmelterTileEntity) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.noble.combustion_engine");
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new ElectricSmelterContainer(i, playerInventory, (ElectricSmelterTileEntity)tileEntity);
                    }
                }; NetworkHooks.openGui((ServerPlayerEntity)player, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        } return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }
}
