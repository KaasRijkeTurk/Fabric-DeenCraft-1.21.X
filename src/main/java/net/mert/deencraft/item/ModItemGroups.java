package net.mert.deencraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.mert.deencraft.DeenCraft;
import net.mert.deencraft.block.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup DEENCRAFT_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(DeenCraft.MOD_ID, "deencraft_items"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.ISLAM_ICON))
                    .displayName(Text.translatable("itemgroup.deencraft.deencraft_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.TASBIH_WOODEN);
                        entries.add(ModItems.TASBIH_IRON);
                        entries.add(ModItems.ASTROLABE);
                        entries.add(ModItems.MISWAK);
                        entries.add(ModItems.DATE);
                        entries.add(ModItems.DATE_AJWA);
                        entries.add(ModItems.DATES);
                        entries.add(ModItems.ELYTRA_IBN);
                        entries.add(ModBlocks.PRAYER_MAT_BLUE);
                        entries.add(ModBlocks.PRAYER_MAT_RED);
                        entries.add(ModBlocks.PRAYER_MAT_GREEN);
                    }).build());

    public static void registerItemGroups(){
        DeenCraft.LOGGER.info("Registering Item Groups for " + DeenCraft.MOD_ID);
    }
}