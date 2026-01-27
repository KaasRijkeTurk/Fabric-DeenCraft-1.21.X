package net.mert.deencraft.item;

import net.mert.deencraft.util.PrayerTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WoodenTasbihItem extends Item {

    public WoodenTasbihItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient() && user instanceof ServerPlayerEntity serverPlayer) {
            PrayerTracker.startOrUpdatePrayer(serverPlayer);
        }

        // Success now handles the "Typed" part internally
        return ActionResult.SUCCESS;
    }

}