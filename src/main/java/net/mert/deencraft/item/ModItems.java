package net.mert.deencraft.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mert.deencraft.DeenCraft;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
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

    public static final Item TASBIH_WOODEN = registerSimpleItem("tasbih_wooden");
    public static final Item TASBIH_IRON = registerSimpleItem("tasbih_iron");
    public static final Item ISLAM_ICON = registerSimpleItem("islam_icon");
    public static final Item MISWAK = registerSimpleItem("miswak");
    public static final Item ASTROLABE = registerSimpleItem("astrolabe");

    public static final Item ELYTRA_IBN = registerConfiguredItem("elytra_ibn", new Item.Settings()
            .maxDamage(216)
            .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.CHEST)
                    .build()));

    public static final Item DATE = registerHydratingFoodItem("date", new Item.Settings()
            .food(ModFoodComponents.DATE)
            .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(
                            List.of(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0)), 1.0f
                    ))
                    .build()
            ), 8);

    public static final Item DATE_AJWA = registerHydratingFoodItem("date_ajwa", new Item.Settings()
            .food(ModFoodComponents.DATE_AJWA)
            .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.food()
                    .consumeEffect(new ApplyEffectsConsumeEffect(
                            List.of(
                                    new StatusEffectInstance(StatusEffects.REGENERATION, 600, 0),
                                    new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0),
                                    new StatusEffectInstance(StatusEffects.SATURATION, 2400, 0)
                            ), 1.0f
                    ))
                    .build()
            ), 10);

    public static final Item DATES = registerHydratingFoodItem("dates", new Item.Settings()
            .food(ModFoodComponents.DATES)
            .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.food().build()), 6);

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(DeenCraft.MOD_ID, name), item);
    }

    private static Item registerConfiguredItem(String name, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name));
        return registerItem(name, new Item(settings.registryKey(key)));
    }

    private static Item registerSimpleItem(String name) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(DeenCraft.MOD_ID, name));
        return registerItem(name, new Item(new Item.Settings().registryKey(key)));
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
            entries.add(DATE_AJWA);
            entries.add(DATES);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(TASBIH_WOODEN);
            entries.add(TASBIH_IRON);
            entries.add(MISWAK);
            entries.add(ASTROLABE);
            entries.add(ISLAM_ICON);
            entries.add(ELYTRA_IBN);
        });
    }
}