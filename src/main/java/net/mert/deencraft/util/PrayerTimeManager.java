package net.mert.deencraft.util;

public class PrayerTimeManager {

    // Start en end ticks van de 5 gebeden (voorbeeld: 0 = Fajr, 1 = Dhuhr ...)
    public static final long[] PRAYER_START = {0, 6000, 12000, 18000, 23000};
    public static final long[] PRAYER_END   = {5999, 11999, 17999, 22999, 23999};

    public static int getCurrentPrayerIndex(long worldTime) {
        for (int i = 0; i < PRAYER_START.length; i++) {
            if (worldTime >= PRAYER_START[i] && worldTime <= PRAYER_END[i]) return i;
        }
        return -1; // Buiten gebedstijd
    }

    public static boolean isWithinPrayerWindow(long worldTime, int prayerIndex) {
        if(prayerIndex < 0 || prayerIndex >= PRAYER_START.length) return false;
        return worldTime >= PRAYER_START[prayerIndex] && worldTime <= PRAYER_END[prayerIndex];
    }
}