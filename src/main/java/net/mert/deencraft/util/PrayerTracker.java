package net.mert.deencraft.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

public class PrayerTracker {

    private static final HashMap<UUID, ActivePrayer> activePrayers = new HashMap<>();

    /** Wordt overal gebruikt (mat + tasbih) */
    public static void startOrUpdatePrayer(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();
        PrayerTime current = PrayerTime.getCurrentPrayer();
        PrayerTime next = current.getNextPrayer();

        long minutesUntilNext = Duration.between(
                LocalTime.now(),
                next.time
        ).toMinutes();

        // 🔁 al bezig → alleen info
        if (activePrayers.containsKey(uuid)) {
            player.sendMessage(Text.literal(
                    "§c[DeenCraft] Je bent bezig met §e" + current.displayName +
                            "§c. Volgend gebed over §e" + minutesUntilNext + " min"
            ), false);
            return;
        }

        // ▶ start nieuw gebed
        activePrayers.put(uuid, new ActivePrayer(current));

        player.sendMessage(Text.literal(
                "§a[DeenCraft] Je bent begonnen met: §e" + current.displayName +
                        "§a (volgend gebed over §e" + minutesUntilNext + " min§a)"
        ), false);
    }

    /** Voor tasbih / checks */
    public static boolean isOnPrayer(ServerPlayerEntity player) {
        return activePrayers.containsKey(player.getUuid());
    }

    public static void stopPrayer(ServerPlayerEntity player) {
        activePrayers.remove(player.getUuid());
    }
}