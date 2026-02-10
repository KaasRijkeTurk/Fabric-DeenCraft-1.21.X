package net.mert.deencraft.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.mert.deencraft.util.IEntityDataSaver;
import net.mert.deencraft.util.ThirstData;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class WaterDrinkHandler {
    public static void register() {
        // Drinken uit de bron (met lege hand)
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND && player.getStackInHand(hand).isEmpty()) {
                // Check of de positie waar je naar kijkt water bevat
                if (world.getFluidState(hitResult.getBlockPos()).isStill() || world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.WATER)) {
                    ThirstData.addThirst((IEntityDataSaver) player, 2, (ServerPlayerEntity) player);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        // Drinken uit items (Bucket / Potion)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            // Snelle check voor client of ongeldige player types
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer) || !(player instanceof IEntityDataSaver dataPlayer)) {
                return ActionResult.PASS;
            }

            if (player.isCreative() || player.isSpectator()) {
                return ActionResult.PASS;
            }

            // Potion check (Water bottle)
            if (stack.isOf(Items.POTION)) {
                // In plaats van String matching is het beter om de Potion contents te checken,
                // maar voor simpele mods werkt dit ook:
                if (stack.getName().getString().toLowerCase().contains("water")) {
                    ThirstData.addThirst(dataPlayer, 5, serverPlayer);
                    // We geven PASS terug zodat de player de animatie van het drinken afmaakt
                    return ActionResult.PASS;
                }
            }

            // Water Bucket check
            if (stack.isOf(Items.WATER_BUCKET)) {
                ThirstData.addThirst(dataPlayer, 15, serverPlayer);

                if (!player.getAbilities().creativeMode) {
                    player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                }
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }
}
