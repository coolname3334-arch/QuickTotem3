package com.quicktotem;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickTotemClient implements ClientModInitializer {
    public static final String MOD_ID = "quicktotem";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("QuickTotem loaded!");
    }
}
