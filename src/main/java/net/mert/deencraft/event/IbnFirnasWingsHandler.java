package net.mert.deencraft.event;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.mert.deencraft.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class IbnFirnasWingsHandler {
    public static void register() {
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            ItemStack chestStack = entity.getEquippedStack(EquipmentSlot.CHEST);

            if (!chestStack.isOf(ModItems.ELYTRA_IBN)) {
                return false;
            }

            if (chestStack.getDamage() >= chestStack.getMaxDamage() - 1) {
                return false;
            }

            if (tickElytra && entity instanceof LivingEntity livingEntity) {
                chestStack.damage(2, livingEntity, EquipmentSlot.CHEST);
            }

            return true;
        });
    }
}