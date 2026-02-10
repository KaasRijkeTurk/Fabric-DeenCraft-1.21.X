package net.mert.deencraft.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mert.deencraft.block.ModBlocks;
import net.mert.deencraft.util.PrayerTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class PrayerMatClickHandler {

    public static void register() {

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if (world.isClient()) return ActionResult.PASS;
            if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;

            if (world.getBlockState(hitResult.getBlockPos()).isOf(ModBlocks.PRAYER_MAT_RED)) {
                PrayerTracker.startOrUpdatePrayer(serverPlayer);
                return ActionResult.SUCCESS;
            }
            if (world.getBlockState(hitResult.getBlockPos()).isOf(ModBlocks.PRAYER_MAT_BLUE)) {
                PrayerTracker.startOrUpdatePrayer(serverPlayer);
                return ActionResult.SUCCESS;
            }
            if (world.getBlockState(hitResult.getBlockPos()).isOf(ModBlocks.PRAYER_MAT_GREEN)) {
                PrayerTracker.startOrUpdatePrayer(serverPlayer);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }
}