package net.mert.deencraft.util;

import java.time.LocalDateTime;

public class ActivePrayer {

    public PrayerTime prayer;
    public PrayerTime nextPrayer;
    public LocalDateTime nextPrayerStart;

    public ActivePrayer(PrayerTime prayer, PrayerTime nextPrayer, LocalDateTime nextPrayerStart) {
        this.prayer = prayer;
        this.nextPrayer = nextPrayer;
        this.nextPrayerStart = nextPrayerStart;
    }
}