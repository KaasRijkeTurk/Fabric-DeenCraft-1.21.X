package net.mert.deencraft.mixin;

import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class DrinkMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    protected void onDrink(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        // Controleer of degene die drinkt een speler is
        if ((Object) this instanceof PlayerEntity player && !world.isClient()) {
            IEntityDataSaver dataPlayer = (IEntityDataSaver) player;

            // Als het item een Potion flesje is (zoals een waterflesje)
            if (stack.getItem() instanceof PotionItem || stack.isOf(Items.HONEY_BOTTLE)) {
                // Voeg 6 punten dorst toe
                dataPlayer.addThirst(6.0f);

                // Optioneel: berichtje
                // player.sendMessage(net.minecraft.text.Text.of("Verfrissend!"), true);
            }

            // Als het item een Milk Bucket is
            if (stack.isOf(Items.MILK_BUCKET)) {
                dataPlayer.addThirst(10.0f);
            }
        }
    }
}
