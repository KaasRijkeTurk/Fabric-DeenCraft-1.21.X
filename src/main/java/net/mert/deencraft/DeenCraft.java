package net.mert.deencraft;

import net.fabricmc.api.ModInitializer;

import net.mert.deencraft.block.ModBlocks;
import net.mert.deencraft.item.ModItemGroups;
import net.mert.deencraft.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeenCraft implements ModInitializer {
	public static final String MOD_ID = "deencraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();

	}
}