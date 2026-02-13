package net.mert.deencraft.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mert.deencraft.item.ModItems;
import net.mert.deencraft.util.IEntityDataSaver;
import net.mert.deencraft.util.ThirstData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerTickHandler {
    private static final Set<UUID> GLIDING_PLAYERS = new HashSet<>();

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                handleIbnFirnasWingsLanding(player);

                if (player.interactionManager.getGameMode() != GameMode.SURVIVAL) {
                    continue;
                }

                if (player instanceof IEntityDataSaver dataPlayer) {

                    if (server.getTicks() % 20 == 0) {
                        BlockPos pos = player.getBlockPos();

                        ServerWorld world = server.getWorld(player.getEntityWorld().getRegistryKey());

                        if (world != null) {
                            int drainInterval = 240;
                            if (world.getBiome(pos).value().getTemperature() >= 1.0f) {
                                drainInterval = 120;
                            }

                            if (server.getTicks() % drainInterval == 0) {
                                ThirstData.addThirst(dataPlayer, -1, player);
                            }

                            int currentThirst = dataPlayer.getPersistentData().getInt("thirst").orElse(20);

                            if (currentThirst < 5) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 0, false, false, true));
                            }

                            if (currentThirst <= 0) {
                                player.damage(world, world.getDamageSources().starve(), 1.0f);
                            }

                            player.sendMessage(Text.of("Hydratatie: " + currentThirst), true);
                        }
                    }
                }
            }
        });
    }

    private static void handleIbnFirnasWingsLanding(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        boolean wasGliding = GLIDING_PLAYERS.contains(playerId);
        boolean isGliding = player.isGliding();

        if (isGliding) {
            GLIDING_PLAYERS.add(playerId);
        } else {
            GLIDING_PLAYERS.remove(playerId);
        }

        if (!wasGliding || isGliding || !player.isOnGround()) {
            return;
        }

        ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chestStack.isOf(ModItems.ELYTRA_IBN)) {
            return;
        }

        if (player.fallDistance > 8.0F) {
            chestStack.damage(40, player, EquipmentSlot.CHEST);
        }
    }
}