package net.mert.deencraft.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mert.deencraft.networking.ThirstDataSyncS2CPacket;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class ThirstData {
    public static void addThirst(IEntityDataSaver player, int amount, ServerPlayerEntity serverPlayer) {
        NbtCompound nbt = player.getPersistentData();
        int thirst = nbt.getInt("thirst").orElse(20);
        thirst = Math.max(0, Math.min(thirst + amount, 20));
        nbt.putInt("thirst", thirst);
        syncThirst(thirst, serverPlayer);
    }

    public static void syncThirst(int thirst, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new ThirstDataSyncS2CPacket(thirst));
    }
}