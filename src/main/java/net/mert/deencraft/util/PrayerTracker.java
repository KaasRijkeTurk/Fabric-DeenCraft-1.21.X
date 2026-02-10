package net.mert.deencraft.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mert.deencraft.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PrayerTracker {

    private static final HashMap<UUID, ActivePrayer> activePrayers = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            LocalDateTime now = LocalDateTime.now();

            if (server.getTicks() % 20 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (isHoldingAstrolabe(player)) {
                        PrayerTime current = PrayerTime.getCurrentPrayer();
                        PrayerTime next = current.getNextPrayer();
                        LocalDateTime nextStart = next.getNextOccurrenceFrom(now);
                        long minutesUntilNext = Math.max(0, Duration.between(now, nextStart).toMinutes());

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
                PrayerTime currentPrayer = PrayerTime.getCurrentPrayer();

                if (!now.isBefore(activePrayer.nextPrayerStart) && activePrayer.prayer != currentPrayer) {
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

    /** Wordt overal gebruikt (mat + tasbih) */
    public static void startOrUpdatePrayer(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();
        LocalDateTime now = LocalDateTime.now();
        PrayerTime current = PrayerTime.getCurrentPrayer();
        PrayerTime next = current.getNextPrayer();
        LocalDateTime nextStart = next.getNextOccurrenceFrom(now);

        long minutesUntilNext = Math.max(0, Duration.between(now, nextStart).toMinutes());

        ActivePrayer existing = activePrayers.get(uuid);

        if (existing != null && existing.prayer == current) {
            player.sendMessage(Text.literal(
                    "§c[DeenCraft] Je bent bezig met §e" + current.displayName +
                            "§c. Volgend gebed over §e" + minutesUntilNext + " min"
            ), false);
            return;
        }

        activePrayers.put(uuid, new ActivePrayer(current, next, nextStart));

        int effectDurationTicks = (int) Math.min(Integer.MAX_VALUE, Math.max(20, Duration.between(now, nextStart).toSeconds() * 20));
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

    private static boolean isHoldingAstrolabe(ServerPlayerEntity player) {
        return player.getMainHandStack().isOf(ModItems.ASTROLABE)
                || player.getOffHandStack().isOf(ModItems.ASTROLABE);
    }
}