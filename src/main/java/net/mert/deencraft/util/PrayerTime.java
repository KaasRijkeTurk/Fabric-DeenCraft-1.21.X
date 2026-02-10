package net.mert.deencraft.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public enum PrayerTime {

    FAJR("Fajr", LocalTime.of(6, 0)),
    DHUHR("Dhuhr", LocalTime.of(13, 0)),
    ASR("Asr", LocalTime.of(17, 0)),
    MAGHRIB("Maghrib", LocalTime.of(20, 0)),
    ISHA("Isha", LocalTime.of(22, 0));

    public final String displayName;
    public final LocalTime time;

    PrayerTime(String name, LocalTime time) {
        this.displayName = name;
        this.time = time;
    }

    public static PrayerTime getCurrentPrayer() {
        LocalTime now = LocalTime.now();
        PrayerTime last = FAJR;

        for (PrayerTime p : values()) {
            if (now.isAfter(p.time)) {
                last = p;
            }
        }
        return last;
    }

    public PrayerTime getNextPrayer() {
        int next = (this.ordinal() + 1) % values().length;
        return values()[next];
    }

    public LocalDateTime getNextOccurrenceFrom(LocalDateTime now) {
        LocalDateTime candidate = now.toLocalDate().atTime(this.time);
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }
}