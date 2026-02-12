package net.mert.deencraft.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mert.deencraft.item.ModItems;
import net.mert.deencraft.networking.BarakahSyncS2CPacket;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PrayerTracker {

    private static final HashMap<UUID, ActivePrayer> activePrayers = new HashMap<>();

    private static final String B_DAY = "barakah_day";
    private static final String B_DAILY_MASK = "barakah_daily_mask";
    private static final String B_DAY_FAILED = "barakah_day_failed";
    private static final String B_DAY_COUNTED = "barakah_day_counted";
    private static final String B_LEVEL = "barakah_level";
    private static final String B_STREAK = "barakah_perfect_day_streak";
    private static final String B_LAST_PRAYER = "barakah_last_prayer";

    private static final int ALL_PRAYERS_MASK = (1 << PrayerTime.values().length) - 1;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    updateDailyProgressState(player);

                    if (isHoldingAstrolabe(player)) {
                        long worldTime = player.getEntityWorld().getTimeOfDay();
                        long dayTime = Math.floorMod(worldTime, PrayerTime.DAY_TICKS);

                        PrayerTime current = PrayerTime.getCurrentPrayer(dayTime);
                        PrayerTime next = current.getNextPrayer();
                        long nextStart = next.getNextOccurrenceFrom(worldTime);
                        long ticksUntilNext = nextStart - worldTime;
                        long secondsUntilNext = ticksToRealtimeSeconds(server, ticksUntilNext);
                        String nextCountdown = formatMinutesSeconds(secondsUntilNext);

                        player.sendMessage(Text.literal(
                                "§e[Astrolabe] §fNu: §b" + current.displayName +
                                        " §f| Volgend: §b" + next.displayName + " §f(over §b" + nextCountdown + "§f)"
                        ), true);
                    }
                }
            }

            Iterator<Map.Entry<UUID, ActivePrayer>> iterator = activePrayers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, ActivePrayer> entry = iterator.next();
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());

                if (player == null) {
                    iterator.remove();
                    continue;
                }

                ActivePrayer activePrayer = entry.getValue();
                long worldTime = player.getEntityWorld().getTimeOfDay();

                if (worldTime >= activePrayer.pendingPrayerDeadlineTick) {
                    player.removeStatusEffect(StatusEffects.RESISTANCE);
                    player.sendMessage(Text.literal(
                            "§c[DeenCraft] Je hebt het gebed §e" + activePrayer.pendingPrayer.displayName +
                                    "§c gemist. Je streak is gereset."
                    ), false);
                    resetBarakahProgress(player);
                    iterator.remove();
                }
            }
        });
    }


    private static boolean isHoldingAstrolabe(ServerPlayerEntity player) {
        return player.getMainHandStack().isOf(ModItems.ASTROLABE)
                || player.getOffHandStack().isOf(ModItems.ASTROLABE);
    }

    /** Wordt overal gebruikt (mat + tasbih) */
    public static void startOrUpdatePrayer(ServerPlayerEntity player) {

        long worldTime = player.getEntityWorld().getTimeOfDay();
        long dayTime = Math.floorMod(worldTime, PrayerTime.DAY_TICKS);

        if (PrayerTime.isSunriseForbidden(dayTime)) {
            player.sendMessage(Text.literal("§c[DeenCraft] Tijdens zonsopkomst kan er niet gebeden worden."), false);
            return;
        }

        updateDailyProgressState(player);

        UUID uuid = player.getUuid();
        PrayerTime current = PrayerTime.getCurrentPrayer(dayTime);
        PrayerTime pendingPrayer = current.getNextPrayer();
        PrayerTime deadlinePrayer = pendingPrayer.getNextPrayer();
        long pendingPrayerDeadlineTick = deadlinePrayer.getNextOccurrenceFrom(worldTime);
        MinecraftServer worldServer = player.getEntityWorld().getServer();
        long secondsUntilDeadline = worldServer != null
                ? ticksToRealtimeSeconds(worldServer, pendingPrayerDeadlineTick - worldTime)
                : ticksToRealtimeSecondsFallback(pendingPrayerDeadlineTick - worldTime);
        String deadlineCountdown = formatMinutesSeconds(secondsUntilDeadline);

        ActivePrayer existing = activePrayers.get(uuid);

        if (existing != null && existing.prayer == current) {
            long secondsForPending = worldServer != null
                    ? ticksToRealtimeSeconds(worldServer, existing.pendingPrayerDeadlineTick - worldTime)
                    : ticksToRealtimeSecondsFallback(existing.pendingPrayerDeadlineTick - worldTime);
            player.sendMessage(Text.literal(
                    "§c[DeenCraft] Dit gebed (§e" + current.displayName + "§c) heb je al gedaan. " +
                            "Bid §e" + existing.pendingPrayer.displayName + "§c binnen §e" + formatMinutesSeconds(secondsForPending)
            ), false);
            return;
        }

        NbtCompound nbt = ((IEntityDataSaver) player).getPersistentData();
        int dailyMask = nbt.getInt(B_DAILY_MASK).orElse(0);
        int prayerBit = 1 << current.ordinal();

        boolean firstTimeThisWindow = (dailyMask & prayerBit) == 0;
        if (firstTimeThisWindow) {
            int xp = 2 + player.getRandom().nextInt(4); // 2-5 XP
            player.addExperience(xp);
            dailyMask |= prayerBit;
            nbt.putInt(B_DAILY_MASK, dailyMask);

            if (!nbt.getBoolean(B_DAY_FAILED).orElse(false)
                    && dailyMask == ALL_PRAYERS_MASK
                    && !nbt.getBoolean(B_DAY_COUNTED).orElse(false)) {
                int streak = nbt.getInt(B_STREAK).orElse(0) + 1;
                int level = Math.min(5, streak);
                nbt.putInt(B_STREAK, streak);
                nbt.putInt(B_LEVEL, level);
                nbt.putBoolean(B_DAY_COUNTED, true);
                applyRandomBarakahEffect(player, level);
                player.sendMessage(Text.literal("§6[DeenCraft] Perfecte dag voltooid! Barakah Level: §e" + level), false);
            }

            syncBarakah(player);
        }

        int streak = nbt.getInt(B_STREAK).orElse(0);
        activePrayers.put(uuid, new ActivePrayer(current, pendingPrayer, pendingPrayerDeadlineTick, streak));

        int effectDurationTicks = (int) Math.min(Integer.MAX_VALUE, Math.max(20, pendingPrayerDeadlineTick - worldTime));
        int baseAmplifier = Math.min(1, Math.max(0, nbt.getInt(B_LEVEL).orElse(0) / 3));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, effectDurationTicks, baseAmplifier, false, false, true));

        int dailyProgress = Integer.bitCount(nbt.getInt(B_DAILY_MASK).orElse(0));

        if (existing == null) {
            player.sendMessage(Text.literal(
                    "§a[DeenCraft] Je bent begonnen met: §e" + current.displayName +
                            "§a. Doe §e" + pendingPrayer.displayName + "§a binnen §e" + deadlineCountdown +
                            "§a. Dag: §e" + dailyProgress + "/5 §a| Barakah: §e" + nbt.getInt(B_LEVEL).orElse(0)
            ), false);
        } else {
            player.sendMessage(Text.literal(
                    "§a[DeenCraft] Gebed geüpdatet naar: §e" + current.displayName +
                            "§a. Doe §e" + pendingPrayer.displayName + "§a binnen §e" + deadlineCountdown +
                            "§a. Dag: §e" + dailyProgress + "/5 §a| Barakah: §e" + nbt.getInt(B_LEVEL).orElse(0)
            ), false);
        }
    }

    /** Voor tasbih / checks */
    public static boolean isOnPrayer(ServerPlayerEntity player) {
        return activePrayers.containsKey(player.getUuid());
    }

    public static void stopPrayer(ServerPlayerEntity player) {
        activePrayers.remove(player.getUuid());
        player.removeStatusEffect(StatusEffects.RESISTANCE);
    }

    private static void updateDailyProgressState(ServerPlayerEntity player) {
        if (!(player instanceof IEntityDataSaver saver)) {
            return;
        }

        long worldTime = player.getEntityWorld().getTimeOfDay();
        long dayNumber = Math.floorDiv(worldTime, PrayerTime.DAY_TICKS);
        long dayTime = Math.floorMod(worldTime, PrayerTime.DAY_TICKS);
        PrayerTime currentPrayer = PrayerTime.getCurrentPrayer(dayTime);

        NbtCompound nbt = saver.getPersistentData();

        if (!nbt.contains(B_DAY)) {
            nbt.putLong(B_DAY, dayNumber);
            nbt.putInt(B_DAILY_MASK, 0);
            nbt.putBoolean(B_DAY_FAILED, false);
            nbt.putBoolean(B_DAY_COUNTED, false);
            nbt.putInt(B_LAST_PRAYER, currentPrayer.ordinal());
            syncBarakah(player);
            return;
        }

        long storedDay = nbt.getLong(B_DAY).orElse(dayNumber);
        if (storedDay != dayNumber) {
            int previousMask = nbt.getInt(B_DAILY_MASK).orElse(0);
            boolean previousFailed = nbt.getBoolean(B_DAY_FAILED).orElse(false);
            boolean previousCounted = nbt.getBoolean(B_DAY_COUNTED).orElse(false);

            if (!previousCounted && (previousFailed || previousMask != ALL_PRAYERS_MASK)) {
                nbt.putInt(B_STREAK, 0);
                nbt.putInt(B_LEVEL, 0);
            }

            nbt.putLong(B_DAY, dayNumber);
            nbt.putInt(B_DAILY_MASK, 0);
            nbt.putBoolean(B_DAY_FAILED, false);
            nbt.putBoolean(B_DAY_COUNTED, false);
            nbt.putInt(B_LAST_PRAYER, currentPrayer.ordinal());
            syncBarakah(player);
            return;
        }

        int lastPrayerOrdinal = nbt.getInt(B_LAST_PRAYER).orElse(currentPrayer.ordinal());
        if (lastPrayerOrdinal != currentPrayer.ordinal()) {
            int dailyMask = nbt.getInt(B_DAILY_MASK).orElse(0);
            int lastPrayerBit = 1 << lastPrayerOrdinal;

            if ((dailyMask & lastPrayerBit) == 0) {
                resetBarakahProgress(player);
                nbt.putBoolean(B_DAY_FAILED, true);
                player.sendMessage(Text.literal("§c[DeenCraft] Je hebt een gebedsvenster gemist. Barakah gereset."), false);
            }

            nbt.putInt(B_LAST_PRAYER, currentPrayer.ordinal());
            syncBarakah(player);
        }
    }

    private static void resetBarakahProgress(ServerPlayerEntity player) {
        if (!(player instanceof IEntityDataSaver saver)) {
            return;
        }

        NbtCompound nbt = saver.getPersistentData();
        nbt.putInt(B_STREAK, 0);
        nbt.putInt(B_LEVEL, 0);
        nbt.putBoolean(B_DAY_COUNTED, false);
        syncBarakah(player);
    }

    private static void syncBarakah(ServerPlayerEntity player) {
        if (!(player instanceof IEntityDataSaver saver)) {
            return;
        }

        NbtCompound nbt = saver.getPersistentData();
        int level = nbt.getInt(B_LEVEL).orElse(0);
        int dailyProgress = Integer.bitCount(nbt.getInt(B_DAILY_MASK).orElse(0));
        int streak = nbt.getInt(B_STREAK).orElse(0);
        ServerPlayNetworking.send(player, new BarakahSyncS2CPacket(level, dailyProgress, streak));
    }

    private static void applyRandomBarakahEffect(ServerPlayerEntity player, int level) {
        if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        int durationTicks = (20 * (20 + (level * 5)));

        player.removeStatusEffect(StatusEffects.REGENERATION);
        player.removeStatusEffect(StatusEffects.ABSORPTION);
        player.removeStatusEffect(StatusEffects.SATURATION);
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        player.removeStatusEffect(StatusEffects.SLOW_FALLING);
        player.removeStatusEffect(StatusEffects.RESISTANCE);

        int roll = player.getRandom().nextInt(6);
        switch (roll) {
            case 0 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, durationTicks, 0, false, true, true));
            case 1 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, durationTicks, 0, false, true, true));
            case 2 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, Math.min(durationTicks, 20 * 10), 0, false, true, true));
            case 3 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, durationTicks, 0, false, true, true));
            case 4 -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, durationTicks, 0, false, true, true));
            default -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, durationTicks, 0, false, true, true));
        }
    }

    private static long ticksToRealtimeSeconds(MinecraftServer server, long ticksRemaining) {
        double tickRate = resolveTickRate(server);

        double seconds = ticksRemaining / tickRate;
        return Math.max(0L, Math.round(seconds));
    }

    private static long ticksToRealtimeSecondsFallback(long ticksRemaining) {
        double seconds = ticksRemaining / 20.0;
        return Math.max(0L, Math.round(seconds));
    }

    private static String formatMinutesSeconds(long secondsTotal) {
        long minutes = secondsTotal / 60;
        long seconds = secondsTotal % 60;
        return minutes + "m " + seconds + "s";
    }


    /**
     * Reads the active/target server tick rate when available, with a safe 20 TPS fallback.
     */
    private static double resolveTickRate(MinecraftServer server) {
        final double fallback = 20.0;

        try {
            Object tickManager = invokeNoArg(server, "getTickManager");
            if (tickManager == null) {
                return fallback;
            }

            Object targetRate = invokeNoArg(tickManager, "getTargetTickRate");
            if (targetRate instanceof Number) {
                double value = ((Number) targetRate).doubleValue();
                if (value > 0.0) {
                    return value;
                }
            }

            Object currentRate = invokeNoArg(tickManager, "getTickRate");
            if (currentRate instanceof Number) {
                double value = ((Number) currentRate).doubleValue();
                if (value > 0.0) {
                    return value;
                }
            }

            Object msPerTick = invokeNoArg(tickManager, "getMillisPerTick");
            if (msPerTick instanceof Number) {
                double value = ((Number) msPerTick).doubleValue();
                if (value > 0.0) {
                    return 1000.0 / value;
                }
            }
        } catch (ReflectiveOperationException ignored) {
        }

        return fallback;
    }

    private static Object invokeNoArg(Object target, String methodName) throws ReflectiveOperationException {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}