package com.cameronpersonett.noble.net;

import com.cameronpersonett.noble.Noble;
import com.cameronpersonett.noble.net.messages.TileEntityUpdateMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NobleNet {
    public static final String NETWORK_VERSION = "0.1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Noble.MOD_ID, "net"),
            () -> NETWORK_VERSION, version -> version.equals(NETWORK_VERSION), version -> version.equals(NETWORK_VERSION));

    public static void init() {
        CHANNEL.registerMessage(0, TileEntityUpdateMessage.class, TileEntityUpdateMessage::encode,
                TileEntityUpdateMessage::decode, TileEntityUpdateMessage::handle);
    }
}
