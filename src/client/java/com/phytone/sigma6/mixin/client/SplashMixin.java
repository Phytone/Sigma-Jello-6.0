package com.phytone.sigma6.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;

@Mixin(LoadingOverlay.class)
public class SplashMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow @Final
    private ReloadInstance reload;

    @Shadow @Final
    private boolean fadeIn;

    @Shadow
    private float currentProgress;

    @Shadow
    private long fadeOutStart;

    @Shadow
    private long fadeInStart;

    @Unique
    private static final Identifier SIGMA_LOGO =
            Identifier.fromNamespaceAndPath(
                    "splash", "sigma_logo"
            );

    @Unique
    private static final int LOGO_WIDTH = 839;

    @Unique
    private static final int LOGO_HEIGHT = 140;

    @Inject(method = "registerTextures", at = @At("TAIL"))
    private static void sigma$registerTextures(
            TextureManager textureManager,
            CallbackInfo ci) {

        try (InputStream stream =
                     SplashMixin.class.getResourceAsStream(
                             "/assets/splash/logo.png"
                     )) {

            if (stream == null) {
                throw new IllegalStateException(
                        "Sigma splash logo not found"
                );
            }

            NativeImage image = NativeImage.read(stream);

            DynamicTexture texture = new DynamicTexture(
                    () -> "Sigma Splash Logo",
                    image
            );

            textureManager.register(SIGMA_LOGO, texture);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load Sigma splash logo", e
            );
        }
    }

    @Inject(
            method = "extractRenderState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sigma$render(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float a,
            CallbackInfo ci) {

        ci.cancel();

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();

        long now = Util.getMillis();

        if (fadeIn && fadeInStart == -1L) {
            fadeInStart = now;
        }

        float fadeOutAnim = fadeOutStart > -1L
                ? (now - fadeOutStart) / 1000.0F
                : -1.0F;

        float fadeInAnim = fadeInStart > -1L
                ? (now - fadeInStart) / 500.0F
                : -1.0F;

        float alpha = 1.0F;

        if (fadeOutAnim >= 1.0F) {

            if (minecraft.gui.screen() != null) {
                minecraft.gui.screen()
                        .extractRenderStateWithTooltipAndSubtitles(
                                graphics, 0, 0, a
                        );
            } else {
                minecraft.gui.hud.extractDeferredSubtitles();
            }

            graphics.nextStratum();

            alpha = 1.0F - Mth.clamp(
                    fadeOutAnim - 1.0F, 0.0F, 1.0F
            );

        } else if (fadeIn) {

            if (minecraft.gui.screen() != null
                    && fadeInAnim < 1.0F) {

                minecraft.gui.screen()
                        .extractRenderStateWithTooltipAndSubtitles(
                                graphics, mouseX, mouseY, a
                        );
            } else {
                minecraft.gui.hud.extractDeferredSubtitles();
            }

            graphics.nextStratum();

            alpha = Mth.clamp(fadeInAnim, 0.0F, 1.0F);
        }

        graphics.fillGradient(
                0, 0, width, height,
                sigma$withAlpha(0x15131B, alpha),
                sigma$withAlpha(0x050505, alpha)
        );

        graphics.nextStratum();

        int logoWidth = Math.max(1, Math.round(
                Math.min(width * 0.54F, height * 0.96F)
        ));

        int logoHeight = Math.max(1, Math.round(
                logoWidth * ((float) LOGO_HEIGHT / LOGO_WIDTH)
        ));

        int logoX = (width - logoWidth) / 2;

        int logoY = Math.round(height * 0.47F)
                - logoHeight / 2;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                SIGMA_LOGO,
                logoX, logoY,
                0.0F, 0.0F,
                logoWidth, logoHeight,
                LOGO_WIDTH, LOGO_HEIGHT,
                LOGO_WIDTH, LOGO_HEIGHT,
                ARGB.white(alpha)
        );

        float actualProgress = reload.getActualProgress();

        currentProgress = Mth.clamp(
                currentProgress * 0.95F
                        + actualProgress * 0.05F,
                0.0F, 1.0F
        );

        if (fadeOutAnim < 1.0F) {

            float barAlpha = alpha * (
                    1.0F - Mth.clamp(
                            fadeOutAnim, 0.0F, 1.0F
                    )
            );

            int barWidth = logoWidth;
            int barHeight = Math.max(
                    6, Math.round(height * 0.04F)
            );

            int barX = (width - barWidth) / 2;
            int barY = Math.round(height * 0.74F);

            sigma$progressBar(
                    graphics,
                    barX, barY,
                    barWidth, barHeight,
                    currentProgress,
                    barAlpha
            );
        }

        if (fadeOutAnim >= 2.0F) {
            minecraft.gui.setOverlay((Overlay) null);
        }
    }

    @Unique
    private static void sigma$progressBar(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int width,
            int height,
            float progress,
            float alpha) {

        sigma$roundedRect(
                graphics,
                x, y, width, height,
                sigma$withAlpha(0x555555, alpha)
        );

        sigma$roundedRect(
                graphics,
                x + 1, y + 1,
                width - 2, height - 2,
                sigma$withAlpha(0x080808, alpha)
        );

        int fillWidth = Math.round(
                (width - 4) * progress
        );

        if (fillWidth > 0) {
            sigma$roundedRect(
                    graphics,
                    x + 2, y + 2,
                    fillWidth, height - 4,
                    sigma$withAlpha(0xEEEEEE, alpha)
            );
        }
    }

    @Unique
    private static void sigma$roundedRect(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int width,
            int height,
            int color) {

        if (width <= 0 || height <= 0) {
            return;
        }

        float radius = Math.min(width, height) / 2.0F;

        for (int row = 0; row < height; row++) {

            float dy = Math.max(
                    0.0F,
                    Math.abs(row + 0.5F - height / 2.0F)
                            - (height / 2.0F - radius)
            );

            int inset = Math.round(
                    radius - (float) Math.sqrt(
                            Math.max(
                                    0.0F,
                                    radius * radius - dy * dy
                            )
                    )
            );

            if (width - inset * 2 > 0) {
                graphics.fill(
                        x + inset,
                        y + row,
                        x + width - inset,
                        y + row + 1,
                        color
                );
            }
        }
    }

    @Unique
    private static int sigma$withAlpha(
            int color, float alpha) {

        return (color & 0xFFFFFF)
                | (Math.round(
                Mth.clamp(alpha, 0.0F, 1.0F) * 255
        ) << 24);
    }
}