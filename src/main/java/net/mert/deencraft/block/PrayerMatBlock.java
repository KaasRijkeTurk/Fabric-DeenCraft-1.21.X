package net.mert.deencraft.block;

import net.mert.deencraft.util.PrayerTracker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarpetBlock;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// Extends CarpetBlock zodat het als tapijt gedropt wordt en nonOpaque kan zijn
public class PrayerMatBlock extends CarpetBlock {


    public PrayerMatBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    // ❗ GEEN @Override (belangrijk in 1.21)
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient() && player instanceof ServerPlayerEntity serverPlayer) {

            // 🔍 HARD DEBUG – dit MOET verschijnen
            serverPlayer.sendMessage(
                    Text.literal("§a[DeenCraft] Prayer Mat clicked!"),
                    false
            );

            PrayerTracker.startOrUpdatePrayer(serverPlayer);
        }

        return ActionResult.SUCCESS;
    }
}