package com.phytone.sigma6.mixin.client;
import com.mojang.blaze3d.platform.NativeImage;

import org.lwjgl.util.freetype.FT_Face;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin {

    @Unique
    private static final double SIGMA_GAMMA = 0.1;

    @Inject(
            method = "copyFromFont",
            at = @At("RETURN")
    )
    private void sigma$enhanceGlyph(
            FT_Face face,
            int index,
            CallbackInfoReturnable<Boolean> cir) {

        if (!cir.getReturnValue()) {
            return;
        }

        NativeImage image = (NativeImage) (Object) this;

        if (image.format() != NativeImage.Format.LUMINANCE) {
            return;
        }

        String family = face.family_nameString();

        if (family == null ||
                !family.toLowerCase(java.util.Locale.ROOT)
                        .contains("nimbus")) {
            return;
        }

        ByteBuffer pixels = image.getPixelBytes();

        for (int i = 0; i < pixels.capacity(); i++) {

            int original = pixels.get(i) & 0xFF;

            if (original == 0 || original == 255) {
                continue;
            }

            int corrected = (int) Math.round(
                    Math.pow(
                            original / 255.0,
                            SIGMA_GAMMA
                    ) * 255.0
            );

            pixels.put(i, (byte) corrected);
        }
    }
}