package net.mert.deencraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap; // Let op deze import
import net.minecraft.client.render.BlockRenderLayer; // Let op deze import
import net.mert.deencraft.block.ModBlocks;

@Environment(EnvType.CLIENT)
public class DeenCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Dit gebruikt de oudere Enum stijl, die in jouw code stond:
        BlockRenderLayerMap.putBlock(ModBlocks.PRAYER_MAT_RED, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.PRAYER_MAT_BLUE, BlockRenderLayer.CUTOUT);
    }
}
