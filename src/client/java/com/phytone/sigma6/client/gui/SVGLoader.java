package com.phytone.sigma6.client.gui;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public class SVGLoader {
    private static final String NAMESPACE = "sigma6";
    public static InputStream load(
            String path, int width, int height) {

        try (InputStream svg =
                     SVGLoader.class.getResourceAsStream(path)) {

            if (svg == null) {
                throw new IllegalArgumentException(
                        "SVG not found: " + path
                );
            }

            PNGTranscoder transcoder = new PNGTranscoder();

            transcoder.addTranscodingHint(
                    PNGTranscoder.KEY_WIDTH, (float) width
            );

            transcoder.addTranscodingHint(
                    PNGTranscoder.KEY_HEIGHT, (float) height
            );

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();

            transcoder.transcode(
                    new TranscoderInput(svg),
                    new TranscoderOutput(output)
            );

            return new ByteArrayInputStream(
                    output.toByteArray()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load SVG: " + path, e
            );
        }
    }

    public static Identifier register(
            String name, String path, int width, int height) {

        try (InputStream stream = load(path, width, height)) {

            NativeImage image = NativeImage.read(stream);

            DynamicTexture texture = new DynamicTexture(
                    () -> name, image
            );

            Identifier id = Identifier.fromNamespaceAndPath(
                    NAMESPACE, name
            );

            Minecraft.getInstance()
                    .getTextureManager()
                    .register(id, texture);

            return id;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to register SVG texture", e
            );
        }
    }
}
