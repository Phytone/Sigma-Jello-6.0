package com.phytone.sigma6;

import com.phytone.sigma6.discord.DiscordRP;
import net.fabricmc.api.ModInitializer;

public class Sigma6 implements ModInitializer {

    @Override
    public void onInitialize() {
        DiscordRP.init();
    }
}
