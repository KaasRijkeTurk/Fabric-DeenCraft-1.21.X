package net.mert.deencraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mert.deencraft.DeenCraft;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    public static final Item TASBIH_WOODEN = registerItem("tasbih_wooden", new Item.Settings());
    public static final Item TASBIH_IRON = registerItem("tasbih_iron", new Item.Settings());
    public static final Item ISLAM_ICON = registerItem("islam_icon", new Item.Settings());
    public static final Item MISWAK = registerItem("miswak", new Item.Settings());
    public static final Item ASTROLABE = registerItem("astrolabe", new Item.Settings());

    public static final Item DATE = registerHydratingFoodItem("date", new Item.Settings()
            .food(ModFoodComponents.DATE), 6);

    public static final Item DATES = registerHydratingFoodItem("dates", new Item.Settings()
            .food(ModFoodComponents.DATES)
            .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(
                            List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0)), 1.0f
                    ))
                    .build()
            ), 1);

    private static Item registerItem(String name, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name));
        Item item = new Item(settings.registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    private static Item registerHydratingFoodItem(String name, Item.Settings settings, int hydrationAmount) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name));
        Item item = new HydratingFoodItem(settings.registryKey(key), hydrationAmount);
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItems() {
        DeenCraft.LOGGER.info("Registering Mod Items for " + DeenCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(DATE);
            entries.add(DATES);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(TASBIH_WOODEN);
            entries.add(TASBIH_IRON);
            entries.add(MISWAK);
            entries.add(ASTROLABE);
            entries.add(ISLAM_ICON);
        });
    }
}