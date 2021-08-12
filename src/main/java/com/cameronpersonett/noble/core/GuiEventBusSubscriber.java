package com.cameronpersonett.noble.core;

import com.cameronpersonett.noble.Noble;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Noble.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GuiEventBusSubscriber {
    public static String previousMessage = "";

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
        if(event.getGui() == null) {
            if(!previousMessage.equals("")) {
                previousMessage = "";
                System.out.println("GUI closed.");
            }
        } else {
            previousMessage = event.getGui().getNarrationMessage();
            System.out.println(event.getGui().getNarrationMessage());
        }
    }
}
