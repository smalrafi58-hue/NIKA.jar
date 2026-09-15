package com.example.animepower;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AnimePower.MODID)
public class AnimePower {

    public static final String MODID = "animepower";

    public AnimePower() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        PowerCommand.register(event.getDispatcher());
    }
}
