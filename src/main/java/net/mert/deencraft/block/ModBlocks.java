package net.mert.deencraft.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mert.deencraft.DeenCraft;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CarpetBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

 public static final Block PRAYER_MAT_RED = registerBlock("prayer_mat_red",
            new CarpetBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(DeenCraft.MOD_ID, "prayer_mat_red")))
                    .strength(0.1f).sounds(BlockSoundGroup.WOOL))
    );
    public static void registerModBlocks() {DeenCraft.LOGGER.info("Registering Mod Blocks for " + DeenCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModBlocks.PRAYER_MAT_RED);
        });
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(DeenCraft.MOD_ID, name),
                new BlockItem(block, new Item.Settings()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name)))));
    }
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(DeenCraft.MOD_ID, name), block);
    }

}


