package net.mert.deencraft.item;

import net.mert.deencraft.util.IEntityDataSaver;
import net.mert.deencraft.util.ThirstData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class HydratingFoodItem extends Item {
    private final int hydrationAmount;

    public HydratingFoodItem(Settings settings, int hydrationAmount) {
        super(settings);
        this.hydrationAmount = hydrationAmount;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack result = super.finishUsing(stack, world, user);

        if (!world.isClient() && user instanceof ServerPlayerEntity player && player instanceof IEntityDataSaver dataPlayer) {
            ThirstData.addThirst(dataPlayer, this.hydrationAmount, player);
        }

        return result;
    }
}