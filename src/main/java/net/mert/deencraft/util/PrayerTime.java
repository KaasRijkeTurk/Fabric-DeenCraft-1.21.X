package net.mert.deencraft.util;

public enum PrayerTime {

    // Minecraft referentie: 6000 = middag, 12000 = zonsondergang, 18000 = middernacht, 23000 = dageraad.
    FAJR("Fajr", 23000),
    DHUHR("Dhuhr", 6000),
    ASR("Asr", 9000),
    MAGHRIB("Maghrib", 12000),
    ISHA("Isha", 14000);

    public static final long DAY_TICKS = 24000L;
    private static final long SUNRISE_FORBIDDEN_START = 0L;
    private static final long SUNRISE_FORBIDDEN_END = 300L;

    public final String displayName;
    public final long startTick;

    PrayerTime(String name, long startTick) {
        this.displayName = name;
        this.startTick = startTick;
    }

    public static PrayerTime getCurrentPrayer(long dayTime) {
        long normalized = Math.floorMod(dayTime, DAY_TICKS);
        PrayerTime current = null;
        long bestTick = Long.MIN_VALUE;

        for (PrayerTime prayerTime : values()) {
            if (prayerTime.startTick <= normalized && prayerTime.startTick > bestTick) {
                current = prayerTime;
                bestTick = prayerTime.startTick;
            }
        }

        if (current != null) {
            return current;
        }

        // Als we vóór de eerste starttick van de dag zitten, val terug op de laatste van vorige dag.
        PrayerTime latest = values()[0];
        for (PrayerTime prayerTime : values()) {
            if (prayerTime.startTick > latest.startTick) {
                latest = prayerTime;
            }
        }
        return latest;
    }

    public PrayerTime getNextPrayer() {
        int next = (this.ordinal() + 1) % values().length;
        return values()[next];
    }

    public long getNextOccurrenceFrom(long worldTime) {
        long dayTime = Math.floorMod(worldTime, DAY_TICKS);
        long dayStart = worldTime - dayTime;
        long nextOccurrence = dayStart + this.startTick;

        if (nextOccurrence <= worldTime) {
            nextOccurrence += DAY_TICKS;
        }

        return nextOccurrence;
    }

    public static boolean isSunriseForbidden(long dayTime) {
        long normalized = Math.floorMod(dayTime, DAY_TICKS);
        return normalized >= SUNRISE_FORBIDDEN_START && normalized < SUNRISE_FORBIDDEN_END;
    }
}