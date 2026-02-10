package net.mert.deencraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.mert.deencraft.block.ModBlocks;
import net.mert.deencraft.client.DropHudOverlay;
import net.mert.deencraft.networking.ThirstDataSyncS2CPacket;
import net.minecraft.client.render.BlockRenderLayer;

public class DeenCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Gebruik de juiste methode voor de Prayer Mats
        BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT,                ModBlocks.PRAYER_MAT_RED, ModBlocks.PRAYER_MAT_BLUE);

        // De cruciale link voor je dorst-data
        ClientPlayNetworking.registerGlobalReceiver(ThirstDataSyncS2CPacket.ID, ThirstDataSyncS2CPacket::receive);

        HudRenderCallback.EVENT.register(new DropHudOverlay());
    }
}