package com.cameronpersonett.noble.blocks.machines.electricsmelter;

import com.cameronpersonett.noble.Noble;
import com.cameronpersonett.noble.blocks.machines.AbstractMachineContainer;
import com.cameronpersonett.noble.blocks.machines.AbstractMachineScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ElectricSmelterScreen extends AbstractMachineScreen {
    public ElectricSmelterScreen(AbstractMachineContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        Gui = new ResourceLocation(Noble.MOD_ID, "textures/gui/electric_smelter.png");
        this.title = "Electric Smelter";
    }

    @Override
    public String getNarrationMessage() {
        return "ElectricSmelterScreen";
    }
}
