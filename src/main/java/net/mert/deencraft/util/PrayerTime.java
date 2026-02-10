package net.mert.deencraft.util;

public enum PrayerTime {

    FAJR("Fajr", 0),
    DHUHR("Dhuhr", 6000),
    ASR("Asr", 12000),
    MAGHRIB("Maghrib", 18000),
    ISHA("Isha", 23000);

    public static final long DAY_TICKS = 24000L;
    private static final long SUNRISE_FORBIDDEN_START = 0L;
    private static final long SUNRISE_FORBIDDEN_END = 1000L;

    public final String displayName;
    public final long startTick;

    PrayerTime(String name, long startTick) {
        this.displayName = name;
        this.startTick = startTick;
    }

    public static PrayerTime getCurrentPrayer(long dayTime) {
        PrayerTime current = ISHA;

        for (PrayerTime prayerTime : values()) {
            if (dayTime >= prayerTime.startTick) {
                current = prayerTime;
            }
        }

        return current;
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