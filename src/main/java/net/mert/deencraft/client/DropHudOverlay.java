package net.mert.deencraft.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.mert.deencraft.DeenCraft;
import net.mert.deencraft.util.IEntityDataSaver;

public class DropHudOverlay implements HudRenderCallback {
    private static final Identifier FILLED_DROP = Identifier.of(DeenCraft.MOD_ID, "textures/gui/drop.png");
    private static final Identifier HALF_DROP = Identifier.of(DeenCraft.MOD_ID, "textures/gui/drop_half.png");
    private static final Identifier EMPTY_DROP = Identifier.of(DeenCraft.MOD_ID, "textures/gui/drop_empty.png");
    private static final int SOURCE_X = 4;
    private static final int SOURCE_Y = 3;
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 8;

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.hudHidden) return;
        if (client.player.isCreative() || client.player.isSpectator()) return;

        int thirstLevel = 0;

        if (client.player instanceof IEntityDataSaver saver) {
            // Gebruik orElse(20) zodat de HUD standaard vol start
            thirstLevel = saver.getPersistentData().getInt("thirst").orElse(20);
        }

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int x = width / 2 + 10;
        int y = height - 49;

        for (int i = 0; i < 10; i++) {
            int xPos = x + (i * ICON_SPACING);

            drawCustomDrop(context, EMPTY_DROP, xPos, y);

            if (thirstLevel >= (i * 2 + 2)) {
                drawCustomDrop(context, FILLED_DROP, xPos, y);
            } else if (thirstLevel == (i * 2 + 1)) {
                drawCustomDrop(context, HALF_DROP, xPos, y);
            }
        }
    }

    private void drawCustomDrop(DrawContext context, Identifier texture, int x, int y) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, (float) SOURCE_X, (float) SOURCE_Y, ICON_SIZE, ICON_SIZE, 16, 16);
    }

}