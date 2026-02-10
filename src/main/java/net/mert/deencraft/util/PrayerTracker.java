package net.mert.deencraft.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mert.deencraft.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
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
                        long worldTime = player.getWorld().getTime();
                        PrayerTime current = PrayerTime.getCurrentPrayer(worldTime % PrayerTime.DAY_TICKS);
                        PrayerTime next = current.getNextPrayer();
                        long nextStart = next.getNextOccurrenceFrom(worldTime);
                        long minutesUntilNext = ticksToRealtimeMinutes(server, nextStart - worldTime);

                        player.sendMessage(Text.literal(
                                "§e[Astrolabe] §fVolgend gebed: §b" + next.displayName +
                                        " §f(over §b" + minutesUntilNext + " min§f)"
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
                long worldTime = player.getWorld().getTime();
                PrayerTime currentPrayer = PrayerTime.getCurrentPrayer(worldTime % PrayerTime.DAY_TICKS);

                if (worldTime >= activePrayer.nextPrayerStartTick && activePrayer.prayer != currentPrayer) {
                    player.removeStatusEffect(StatusEffects.RESISTANCE);
                    player.sendMessage(Text.literal(
                            "§c[DeenCraft] Je hebt het gebed §e" + activePrayer.nextPrayer.displayName +
                                    "§c gemist. Je boost is verwijderd."
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

        long worldTime = player.getWorld().getTime();
        long dayTime = worldTime % PrayerTime.DAY_TICKS;

        if (PrayerTime.isSunriseForbidden(dayTime)) {
            player.sendMessage(Text.literal("§c[DeenCraft] Tijdens zonsopkomst kan er niet gebeden worden."), false);
            return;
        }

        UUID uuid = player.getUuid();
        PrayerTime current = PrayerTime.getCurrentPrayer(dayTime);
        PrayerTime next = current.getNextPrayer();
        long nextStart = next.getNextOccurrenceFrom(worldTime);
        long minutesUntilNext = ticksToRealtimeMinutes(player.getServer(), nextStart - worldTime);

        ActivePrayer existing = activePrayers.get(uuid);

        if (existing != null && existing.prayer == current) {
            player.sendMessage(Text.literal(
                    "§c[DeenCraft] Je bent bezig met §e" + current.displayName +
                            "§c. Volgend gebed over §e" + minutesUntilNext + " min"
            ), false);
            return;
        }

        activePrayers.put(uuid, new ActivePrayer(current, next, nextStart));

        int effectDurationTicks = (int) Math.min(Integer.MAX_VALUE, Math.max(20, nextStart - worldTime));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, effectDurationTicks, 0, false, false, true));

        if (existing == null) {
            player.sendMessage(Text.literal(
                    "§a[DeenCraft] Je bent begonnen met: §e" + current.displayName +
                            "§a (volgend gebed over §e" + minutesUntilNext + " min§a)"
            ), false);
        } else {
            player.sendMessage(Text.literal(
                    "§a[DeenCraft] Gebed geüpdatet naar: §e" + current.displayName +
                            "§a (volgend gebed over §e" + minutesUntilNext + " min§a)"
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

    private static long ticksToRealtimeMinutes(MinecraftServer server, long ticksRemaining) {
        double tickRate = resolveTickRate(server);

        double minutes = ticksRemaining / (tickRate * 60.0);
        return Math.max(0L, Math.round(minutes));
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
            if (targetRate instanceof Number number && number.doubleValue() > 0.0) {
                return number.doubleValue();
            }

            Object currentRate = invokeNoArg(tickManager, "getTickRate");
            if (currentRate instanceof Number number && number.doubleValue() > 0.0) {
                return number.doubleValue();
            }

            // Some versions expose tick duration (mspt) instead of a direct rate.
            Object msPerTick = invokeNoArg(tickManager, "getMillisPerTick");
            if (msPerTick instanceof Number number && number.doubleValue() > 0.0) {
                return 1000.0 / number.doubleValue();
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