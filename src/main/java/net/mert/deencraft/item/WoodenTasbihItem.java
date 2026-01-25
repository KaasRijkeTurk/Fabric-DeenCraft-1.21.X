package net.mert.deencraft.item;

import net.mert.deencraft.util.PrayerTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WoodenTasbihItem extends Item {

    public WoodenTasbihItem(Settings settings) {
        super(settings);
    }

    // FIX: Gebruik ActionResult als returntype
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && PrayerTracker.isOnPrayer(user)) {
            System.out.println("Gebed is al gedaan, Tasbih gebruikt!");
        }
        return ActionResult.PASS;
    }
}
