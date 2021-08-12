package com.cameronpersonett.noble.blocks.engines.combustion;

import com.cameronpersonett.noble.Noble;
import com.cameronpersonett.noble.blocks.engines.AbstractEngineContainer;
import com.cameronpersonett.noble.blocks.engines.AbstractEngineScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CombustionEngineScreen extends AbstractEngineScreen {
    public CombustionEngineScreen(AbstractEngineContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        Gui = new ResourceLocation(Noble.MOD_ID, "textures/gui/combustion_engine.png");
    }

    @Override
    public String getNarrationMessage() {
        return "CombustionEngineScreen";
    }
}
