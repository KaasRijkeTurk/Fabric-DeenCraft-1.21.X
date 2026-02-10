package net.mert.deencraft.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mert.deencraft.util.IEntityDataSaver;
import net.mert.deencraft.util.ThirstData;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaterDrinkHandler {
    private static final Map<UUID, Hand> PENDING_WATER_POTION_DRINKS = new HashMap<>();

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

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer) || !(player instanceof IEntityDataSaver dataPlayer)) {
                return ActionResult.PASS;
            }

            if (player.isCreative() || player.isSpectator()) {
                return ActionResult.PASS;
            }

            String itemName = stack.getName().getString().toLowerCase();
            if (stack.isOf(Items.POTION) && itemName.contains("water")) {
                PENDING_WATER_POTION_DRINKS.put(player.getUuid(), hand);
                return ActionResult.PASS;
            }

            return ActionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                Hand trackedHand = PENDING_WATER_POTION_DRINKS.get(player.getUuid());
                if (trackedHand == null) {
                    continue;
                }

                if (player.isUsingItem()) {
                    continue;
                }

                ItemStack stack = player.getStackInHand(trackedHand);
                if (stack.isOf(Items.GLASS_BOTTLE) && player instanceof IEntityDataSaver dataPlayer) {
                    ThirstData.addThirst(dataPlayer, 5, player);
                }

                PENDING_WATER_POTION_DRINKS.remove(player.getUuid());
            }
        });
    }
}