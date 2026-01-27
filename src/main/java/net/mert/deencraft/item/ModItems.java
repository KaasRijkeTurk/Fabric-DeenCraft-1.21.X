package net.mert.deencraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mert.deencraft.DeenCraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item TASBIH_WOODEN = registerItem("tasbih_wooden", new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID,"tasbih_wooden"))));
    public static final Item TASBIH_IRON = registerItem("tasbih_iron", new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID,"tasbih_iron"))));
    public static final Item ISLAM_ICON = registerItem("islam_icon", new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, "islam_icon"))));
    public static final Item MISWAK = registerItem("miswak", new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, "miswak"))));


    private static Item registerItem(String name, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name));
        Item item = new Item(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItems(){
        DeenCraft.LOGGER.info("Registering Mod Items for "+ DeenCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {

            entries.add(TASBIH_WOODEN);
            entries.add(TASBIH_IRON);
            entries.add(MISWAK);
            entries.add(ISLAM_ICON);
        });
    }
}
