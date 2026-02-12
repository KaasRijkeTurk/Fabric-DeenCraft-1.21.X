package net.mert.deencraft.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mert.deencraft.util.IEntityDataSaver;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BarakahSyncS2CPacket(int level, int dailyProgress, int perfectDayStreak) implements CustomPayload {
    public static final Id<BarakahSyncS2CPacket> ID = new Id<>(Identifier.of("deencraft", "barakah_sync"));

    public static final PacketCodec<RegistryByteBuf, BarakahSyncS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, BarakahSyncS2CPacket::level,
            PacketCodecs.VAR_INT, BarakahSyncS2CPacket::dailyProgress,
            PacketCodecs.VAR_INT, BarakahSyncS2CPacket::perfectDayStreak,
            BarakahSyncS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(BarakahSyncS2CPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if (context.player() instanceof IEntityDataSaver saver) {
                saver.getPersistentData().putInt("barakah_level", payload.level());
                saver.getPersistentData().putInt("barakah_daily_progress", payload.dailyProgress());
                saver.getPersistentData().putInt("barakah_perfect_day_streak", payload.perfectDayStreak());
            }
        });
    }
}