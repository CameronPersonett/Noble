package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.blocks.machines.AbstractMachineTileEntity;
import com.cameronpersonett.noble.core.Registration;

public class ElectricSmelterTileEntity extends AbstractMachineTileEntity {
    public ElectricSmelterTileEntity() {
        super(Registration.ELECTRIC_SMELTER_TILE_ENTITY.get());
    }

    @Override
    public void tick() {
        if(!level.isClientSide) {

        }
    }
}