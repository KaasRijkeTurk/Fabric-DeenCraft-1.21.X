package net.mert.deencraft.util;

public class ActivePrayer {

    public PrayerTime prayer;
    public PrayerTime pendingPrayer;
    public long pendingPrayerDeadlineTick;
    public int streak;

    public ActivePrayer(PrayerTime prayer, PrayerTime pendingPrayer, long pendingPrayerDeadlineTick, int streak) {
        this.prayer = prayer;
        this.pendingPrayer = pendingPrayer;
        this.pendingPrayerDeadlineTick = pendingPrayerDeadlineTick;
        this.streak = streak;
    }
}