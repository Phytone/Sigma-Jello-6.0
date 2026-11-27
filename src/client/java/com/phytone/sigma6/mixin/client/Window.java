package com.phytone.sigma6.mixin.client;
import net.minecraft.server.packs.resources.IoSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.InputStream;
import java.util.List;

@Mixin(com.mojang.blaze3d.platform.Window.class)
public class Window {
    @ModifyVariable(
            method = "setIcon(Ljava/util/List;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private List<IoSupplier<InputStream>> changeIcon(
            List<IoSupplier<InputStream>> original) {

        return List.of(
                () -> Window.class.getResourceAsStream(
                        "/assets/icon/icon_32x32.png"),
                () -> Window.class.getResourceAsStream(
                        "/assets/icon/icon_16x16.png")
        );
    }
}
