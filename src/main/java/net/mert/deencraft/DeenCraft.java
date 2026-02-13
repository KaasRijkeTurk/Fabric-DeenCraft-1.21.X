package net.mert.deencraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.mert.deencraft.block.ModBlocks;
import net.mert.deencraft.event.IbnFirnasWingsHandler;
import net.mert.deencraft.event.PlayerTickHandler;
import net.mert.deencraft.event.PrayerMatClickHandler;
import net.mert.deencraft.event.WaterDrinkHandler;
import net.mert.deencraft.item.ModItemGroups;
import net.mert.deencraft.item.ModItems;
import net.mert.deencraft.networking.BarakahSyncS2CPacket;
import net.mert.deencraft.networking.ThirstDataSyncS2CPacket;
import net.mert.deencraft.util.PrayerTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeenCraft implements ModInitializer {

	public static final String MOD_ID = "deencraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing DeenCraft");
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		PrayerMatClickHandler.register();
		WaterDrinkHandler.register(); // Deze is goed!
		IbnFirnasWingsHandler.register();
		PlayerTickHandler.register();
		PrayerTracker.register();

		// Alleen de registratie van het TYPE pakketje hier laten staan
		PayloadTypeRegistry.playS2C().register(ThirstDataSyncS2CPacket.ID, ThirstDataSyncS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(BarakahSyncS2CPacket.ID, BarakahSyncS2CPacket.CODEC);

		// De UseBlockCallback die je hieronder hebt staan is dubbelop met je WaterDrinkHandler.
		// Je kunt deze hier weghalen als WaterDrinkHandler.register() hetzelfde doet!
	}

}