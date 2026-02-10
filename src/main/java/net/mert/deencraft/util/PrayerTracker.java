package net.mert.deencraft.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mert.deencraft.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PrayerTracker {

    private static final HashMap<UUID, ActivePrayer> activePrayers = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 20 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
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

                // Gebed telt pas als "gemist" wanneer de volledige venster-tijd voorbij is.
                // Voorbeeld: Fajr gedaan -> Dhuhr is pending -> pas bij start Asr is Dhuhr gemist.
                if (worldTime >= activePrayer.pendingPrayerDeadlineTick) {
                    player.removeStatusEffect(StatusEffects.RESISTANCE);
                    player.sendMessage(Text.literal(
                            "§c[DeenCraft] Je hebt het gebed §e" + activePrayer.pendingPrayer.displayName +
                                    "§c gemist. Je streak is gereset."
                    ), false);
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

        int streak = 1;
        if (existing != null) {
            // Alleen streak verhogen als je exact het volgende gebed op tijd hebt gedaan.
            if (current == existing.pendingPrayer && worldTime < existing.pendingPrayerDeadlineTick) {
                streak = existing.streak + 1;
            }
        }

        activePrayers.put(uuid, new ActivePrayer(current, pendingPrayer, pendingPrayerDeadlineTick, streak));

        int amplifier = Math.min(3, Math.max(0, streak - 1)); // 0=Resistance I, 1=II, ...
        int effectDurationTicks = (int) Math.min(Integer.MAX_VALUE, Math.max(20, pendingPrayerDeadlineTick - worldTime));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, effectDurationTicks, amplifier, false, false, true));

        String streakInfo = "§bStreak: " + streak;
        if (existing == null) {
            player.sendMessage(Text.literal(
                    "§a[DeenCraft] Je bent begonnen met: §e" + current.displayName +
                            "§a. Doe §e" + pendingPrayer.displayName + "§a binnen §e" + deadlineCountdown +
                            "§a. " + streakInfo
            ), false);
        } else {
            player.sendMessage(Text.literal(
                    "§a[DeenCraft] Gebed geüpdatet naar: §e" + current.displayName +
                            "§a. Doe §e" + pendingPrayer.displayName + "§a binnen §e" + deadlineCountdown +
                            "§a. " + streakInfo
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

            // Newer versions often expose target tick rate here (used by /tick rate).
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

            // Some versions expose tick duration (mspt) instead of a direct rate.
            Object msPerTick = invokeNoArg(tickManager, "getMillisPerTick");
            if (msPerTick instanceof Number) {
                double value = ((Number) msPerTick).doubleValue();
                if (value > 0.0) {
                    return 1000.0 / value;
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // Fallback op standaard TPS als methodes verschillen per Minecraft-versie.
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