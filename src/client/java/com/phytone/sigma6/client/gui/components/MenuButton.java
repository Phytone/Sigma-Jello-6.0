package com.phytone.sigma6.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;

import com.phytone.sigma6.client.gui.SVGLoader;

import java.util.HashMap;
import java.util.Map;

public class MenuButton {

    private static final int TEXTURE_SIZE = 256;

    private static final Map<String, Identifier> TEXTURES =
            new HashMap<>();

    private final Identifier texture;
    private final String name;
    private final Runnable action;

    private int x;
    private int y;
    private int size;

    private float hoverAnimation = 0.0F;

    public MenuButton(
            String name,
            String textureName,
            int x,
            int y,
            int size,
            Runnable action) {

        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        this.action = action;

        this.texture = TEXTURES.computeIfAbsent(
                textureName,
                key -> SVGLoader.register(
                        "menu_button_" + key,
                        "/assets/buttons/" + key + ".svg",
                        TEXTURE_SIZE,
                        TEXTURE_SIZE
                )
        );
    }

    public boolean isHovered(double mouseX, double mouseY) {

        double radius = size / 2.0;

        double dx = mouseX - (x + radius);
        double dy = mouseY - (y + radius);

        return dx * dx + dy * dy <= radius * radius;
    }

    public void render(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY) {

        float target = isHovered(mouseX, mouseY)
                ? 1.0F : 0.0F;

        hoverAnimation +=
                (target - hoverAnimation) * 0.15F;

        float scale = 1.0F + hoverAnimation * 0.10F;

        int renderSize = Math.round(size * scale);

        int renderX = x + (size - renderSize) / 2;
        int renderY = y + (size - renderSize) / 2;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                renderX, renderY,
                0.0F, 0.0F,
                renderSize, renderSize,
                TEXTURE_SIZE, TEXTURE_SIZE,
                TEXTURE_SIZE, TEXTURE_SIZE
        );
    }

    public boolean click(double mouseX, double mouseY) {

        if (!isHovered(mouseX, mouseY)) {
            return false;
        }

        Minecraft.getInstance()
                .getSoundManager()
                .play(SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK,
                        1.0F
                ));

        action.run();

        return true;
    }

    public String getName() {
        return name;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}