package net.mert.deencraft.item;

import net.minecraft.component.type.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent DATES = new FoodComponent.Builder()
            .nutrition(2)
            .saturationModifier(1.2f)
            .build();

    public static final FoodComponent DATE = new FoodComponent.Builder()
            .nutrition(5)
            .saturationModifier(1.0f)
            .alwaysEdible()
            .build();
}
