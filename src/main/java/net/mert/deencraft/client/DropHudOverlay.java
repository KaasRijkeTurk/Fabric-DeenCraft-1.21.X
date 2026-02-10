package net.mert.deencraft.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.mert.deencraft.DeenCraft;
import net.mert.deencraft.util.IEntityDataSaver;

public class DropHudOverlay implements HudRenderCallback {
    private static final Identifier FILLED_DROP = Identifier.of(DeenCraft.MOD_ID, "textures/gui/drop.png");
    private static final Identifier HALF_DROP = Identifier.of(DeenCraft.MOD_ID, "textures/gui/drop_half.png");
    private static final Identifier EMPTY_DROP = Identifier.of(DeenCraft.MOD_ID, "textures/gui/drop_empty.png");

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.options.hudHidden) return;

        int thirstLevel = 0;

        if (client.player instanceof IEntityDataSaver saver) {
            // Gebruik orElse(0) voor de Optional<Integer> uit de NBT
            thirstLevel = saver.getPersistentData().getInt("thirst").orElse(0);
        }

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        int x = width / 2 + 10;
        int y = height - 55;

        for (int i = 0; i < 10; i++) {
            int xPos = x + (i * 8);

            // Stap over naar RenderLayer::getGui voor correcte shader-afhandeling in 1.21.10
            drawCustomDrop(context, EMPTY_DROP, xPos, y);

            if (thirstLevel >= (i * 2 + 2)) {
                drawCustomDrop(context, FILLED_DROP, xPos, y);
            } else if (thirstLevel == (i * 2 + 1)) {
                drawCustomDrop(context, HALF_DROP, xPos, y);
            }
        }
    }

    private void drawCustomDrop(DrawContext context, Identifier texture, int x, int y) {
        // Gebruik RenderLayer::getGui voor standaard 2D GUI elementen met texture
        // Dit voorkomt fouten door misaligned shader states in 1.21.x
        context.drawTexture(RenderLayer::getGui, texture, x, y, 0f, 0f, 9, 9, 9, 9);
    }
}