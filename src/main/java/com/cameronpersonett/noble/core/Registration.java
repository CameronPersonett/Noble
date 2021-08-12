package com.cameronpersonett.noble.core;

import com.cameronpersonett.noble.Noble;
import com.cameronpersonett.noble.blocks.engines.AbstractEngineContainer;
import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineBlock;
import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineContainer;
import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineTileEntity;
import com.cameronpersonett.noble.blocks.machines.electricsmelter.ElectricSmelterBlock;
import com.cameronpersonett.noble.blocks.machines.electricsmelter.ElectricSmelterContainer;
import com.cameronpersonett.noble.blocks.machines.electricsmelter.ElectricSmelterTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Noble.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Noble.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Noble.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Noble.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Noble.MOD_ID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<CombustionEngineBlock> COMBUSTION_ENGINE_BLOCK = BLOCKS.register("combustion_engine", CombustionEngineBlock::new);
    public static final RegistryObject<TileEntityType<CombustionEngineTileEntity>> COMBUSTION_ENGINE_TILE_ENTITY =
            TILE_ENTITIES.register("combustion_engine", () -> TileEntityType.Builder.of(CombustionEngineTileEntity::new,
                    COMBUSTION_ENGINE_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<AbstractEngineContainer>> COMBUSTION_ENGINE_CONTAINER =
            CONTAINERS.register("combustion_engine", () -> IForgeContainerType.create(CombustionEngineContainer::new));

    public static final RegistryObject<ElectricSmelterBlock> ELECTRIC_SMELTER_BLOCK = BLOCKS.register("electric_smelter", ElectricSmelterBlock::new);
    public static final RegistryObject<TileEntityType<ElectricSmelterTileEntity>> ELECTRIC_SMELTER_TILE_ENTITY =
            TILE_ENTITIES.register("electric_smelter", () -> TileEntityType.Builder.of(ElectricSmelterTileEntity::new,
                    ELECTRIC_SMELTER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<ElectricSmelterContainer>> ELECTRIC_SMELTER_CONTAINER =
            CONTAINERS.register("electric_smelter", () -> IForgeContainerType.create(ElectricSmelterContainer::new));
}
