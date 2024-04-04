package me.julionxn.biomechest;

import me.julionxn.biomechest.networking.AllPackets;
import net.fabricmc.api.ClientModInitializer;

public class BiomeChestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AllPackets.registerS2CPackets();
    }
}
