package net.mert.deencraft.util;

public class ActivePrayer {

    public PrayerTime prayer;
    public PrayerTime nextPrayer;
    public long nextPrayerStartTick;

    public ActivePrayer(PrayerTime prayer, PrayerTime nextPrayer, long nextPrayerStartTick) {
        this.prayer = prayer;
        this.nextPrayer = nextPrayer;
        this.nextPrayerStartTick = nextPrayerStartTick;
    }
}