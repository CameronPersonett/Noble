package com.cameronpersonett.noble;

import com.cameronpersonett.noble.core.Registration;
import com.cameronpersonett.noble.net.NobleNet;
import com.cameronpersonett.noble.tools.NobleItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("noble")
@Mod.EventBusSubscriber(modid = Noble.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Noble {
    public static final String MOD_ID = "noble";
    private static final Logger LOGGER = LogManager.getLogger();

    public Noble() {
        Registration.init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
        Registration.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
            if(block.getRegistryName() != null) {
                event.getRegistry().register(new BlockItem(block, new Item.Properties().tab(NobleItemGroup.NOBLE)).setRegistryName(block.getRegistryName()));
            } else {
                event.getRegistry().register(new BlockItem(block, new Item.Properties().tab(NobleItemGroup.NOBLE)).setRegistryName("null"));
            }
        });
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        NobleNet.init();
    }
}
