package net.mert.deencraft.util;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class PrayerTracker {

    private static final String NBT_KEY = "completed_prayers";

    public static void registerPrayer(PlayerEntity player) {
        World world = player.getEntityWorld();
        IEntityDataSaver dataSaver = (IEntityDataSaver) player;
        NbtCompound nbt = dataSaver.getPersistentData();

        long time = world.getTime();
        int currentPrayer = PrayerTimeManager.getCurrentPrayerIndex(time);

        if (currentPrayer == -1) return;

        byte[] completed = nbt.getByteArray(NBT_KEY);
        if (completed.length != 5) {
            completed = new byte[5]; // ✅ lengte 5
        }
        completed[currentPrayer] = 1;
        nbt.putByteArray(NBT_KEY, completed);
    }

    public static boolean isOnPrayer(PlayerEntity player) {
        if (!(player instanceof IEntityDataSaver)) return false;

        int currentPrayer = PrayerTimeManager.getCurrentPrayerIndex(player.getEntityWorld().getTime());

        byte[] completed = ((IEntityDataSaver) player).getPersistentData()
                .getByteArray(NBT_KEY);

        if (currentPrayer == -1 || completed.length != 5) return false;

        return completed[currentPrayer] == 1;
    }

    public static void applyPrayerBuffs(PlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 200, 0, false, false, true));
    }

    public static void resetAllPrayers(PlayerEntity player) {
        player.removeStatusEffect(StatusEffects.SATURATION);
        IEntityDataSaver dataSaver = (IEntityDataSaver) player;
        NbtCompound nbt = dataSaver.getPersistentData();
        // FIX: nieuwe byte array van lengte 5
        nbt.putByteArray(NBT_KEY, new byte[5]); // TYP HIER HANDMATIG [5]
    }
}
