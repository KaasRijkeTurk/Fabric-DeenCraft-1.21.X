package net.mert.deencraft.item;

import net.minecraft.component.type.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent DATES = new FoodComponent.Builder()
            .nutrition(2)
            .saturationModifier(1.2f)
            .alwaysEdible()
            .build();


    public static final FoodComponent DATE = new FoodComponent.Builder()
            .nutrition(5)
            .saturationModifier(1.0f)
            .alwaysEdible()
            .build();

    public static final FoodComponent DATE_AJWA = new FoodComponent.Builder()
            .nutrition(6)
            .saturationModifier(1.3f)
            .alwaysEdible()
            .build();

}

