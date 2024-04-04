package me.julionxn.biomechest;

import me.julionxn.biomechest.command.BiomeChestCommand;
import me.julionxn.biomechest.networking.AllPackets;
import me.julionxn.biomechest.persistent.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiomeChest implements ModInitializer {
	public static final String ID = "biomechest";
    public static final Logger LOGGER = LoggerFactory.getLogger("biomechest");

	@Override
	public void onInitialize() {
		Config.getInstance().init();
		CommandRegistrationCallback.EVENT.register(BiomeChestCommand::register);
		AllPackets.registerC2SPackets();
		LOGGER.info("Hello Fabric world!");
	}
}