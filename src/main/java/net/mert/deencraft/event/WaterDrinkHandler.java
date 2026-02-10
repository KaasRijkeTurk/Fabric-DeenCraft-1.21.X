package net.mert.deencraft.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.mert.deencraft.util.IEntityDataSaver;
import net.mert.deencraft.util.ThirstData;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class WaterDrinkHandler {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND && player.getStackInHand(hand).isEmpty()) {
                if (world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.WATER)) {
                    ThirstData.addThirst((IEntityDataSaver) player, 2, (ServerPlayerEntity) player);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}