package com.cameronpersonett.noble.core;

import com.cameronpersonett.noble.Noble;
import com.cameronpersonett.noble.blocks.engines.combustion.CombustionEngineScreen;
import com.cameronpersonett.noble.blocks.machines.electricsmelter.ElectricSmelterScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Noble.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.register(Registration.COMBUSTION_ENGINE_CONTAINER.get(), CombustionEngineScreen::new);
        ScreenManager.register(Registration.ELECTRIC_SMELTER_CONTAINER.get(), ElectricSmelterScreen::new);
    }
}