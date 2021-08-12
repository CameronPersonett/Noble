package com.cameronpersonett.noble.blocks.engines;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
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

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractEngineBlock extends Block {
    protected final String title;

    public AbstractEngineBlock(String title) {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.0f));
        this.title = title;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag flags) {
        list.add(new TranslationTextComponent("message." + title));
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return state.getValue(BlockStateProperties.POWERED) ? super.getLightValue(state, world, pos) : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public abstract ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace);

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }
}
