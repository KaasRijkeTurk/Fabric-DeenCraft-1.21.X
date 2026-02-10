package net.mert.deencraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mert.deencraft.DeenCraft;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect; // Probeer deze import!
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    // --- FUNCTIONELE ITEMS ---
    public static final Item TASBIH_WOODEN = registerItem("tasbih_wooden", new Item.Settings());
    public static final Item TASBIH_IRON = registerItem("tasbih_iron", new Item.Settings());
    public static final Item ISLAM_ICON = registerItem("islam_icon", new Item.Settings());
    public static final Item MISWAK = registerItem("miswak", new Item.Settings());
    public static final Item ASTROLABE = registerItem("astrolabe", new Item.Settings());

    // --- FOOD ITEMS ---
    public static final Item DATE = registerItem("date", new Item.Settings()
            .food(ModFoodComponents.DATE)
            .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.food()
                    // LET OP: In 1.21.10 verwacht ApplyEffectsConsumeEffect vaak een List.of()
                    .consumeEffect(new ApplyEffectsConsumeEffect(
                            List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 1000, 0)), 1.0f
                    ))
                    .build()
            ));

    public static final Item DATES = registerItem("dates", new Item.Settings()
            .food(ModFoodComponents.DATES));

    // --- REGISTRATIE LOGICA ---
    private static Item registerItem(String name, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name));
        // Registreer het item en koppel de key aan de settings
        Item item = new Item(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItems() {
        DeenCraft.LOGGER.info("Registering Mod Items for " + DeenCraft.MOD_ID);

        // Voeg toe aan Food tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(DATE);
            entries.add(DATES);
        });

        // Voeg toe aan Functional tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(TASBIH_WOODEN);
            entries.add(TASBIH_IRON);
            entries.add(MISWAK);
            entries.add(ASTROLABE);
            entries.add(ISLAM_ICON);
        });
    }
}
