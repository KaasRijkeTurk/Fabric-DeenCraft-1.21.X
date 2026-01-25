package net.mert.deencraft.block;

import net.mert.deencraft.util.PrayerTracker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PrayerMatBlock extends CarpetBlock {

    public PrayerMatBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    // FIX: Gebruik de Intermediary naam 'method_9534'
    @Override
    public ActionResult method_9534(BlockState state, World world, BlockPos pos,
                                    PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient()) {
            PrayerTracker.registerPrayer(player);
        }
        return ActionResult.SUCCESS;
    }
}
