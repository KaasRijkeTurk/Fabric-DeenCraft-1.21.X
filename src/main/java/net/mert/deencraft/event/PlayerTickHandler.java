package net.mert.deencraft.event;

import net.mert.deencraft.util.IEntityDataSaver;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.text.Text;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;

public class PlayerTickHandler {
    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player instanceof IEntityDataSaver dataPlayer) {

                    if (server.getTicks() % 20 == 0) {
                        float currentRate = -0.01f;
                        BlockPos pos = player.getBlockPos();

                        // FIX: Gebruik de server om de juiste world te pakken
                        ServerWorld world = server.getWorld(player.getEntityWorld().getRegistryKey());

                        if (world != null) {
                            // Biome check
                            if (world.getBiome(pos).value().getTemperature() >= 1.0f) {
                                currentRate *= 10.0f;
                            }

                            dataPlayer.addThirst(currentRate);
                            float currentThirst = dataPlayer.getThirst();

                            // Effecten
                            if (currentThirst < 5.0f) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0, false, false, true));
                            }

                            // Schade
                            if (currentThirst <= 0.0f) {
                                player.damage(world, world.getDamageSources().starve(), 1.0f);
                            }

                            player.sendMessage(Text.of("Hydratatie: " + String.format("%.1f", currentThirst)), true);
                        }
                    }
                }
            }
        });
    }
}