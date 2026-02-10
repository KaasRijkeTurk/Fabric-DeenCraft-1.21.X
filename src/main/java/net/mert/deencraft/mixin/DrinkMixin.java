package net.mert.deencraft.mixin;

import net.mert.deencraft.item.ModItems;
import net.mert.deencraft.util.IEntityDataSaver;
import net.mert.deencraft.util.ThirstData;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class DrinkMixin {

    @Inject(method = "eatFood", at = @At("TAIL"))
    private void deencraft$onEatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient() && (Object) this instanceof ServerPlayerEntity player && player instanceof IEntityDataSaver dataPlayer) {
            if (stack.isOf(ModItems.DATE) || stack.isOf(ModItems.DATES)) {
                ThirstData.addThirst(dataPlayer, 4, player);
            }
        }
    }
}