package com.phytone.sigma6.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.Minecraft.class)
public class Minecraft {

    @Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
    private void changeTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Sigma Jello 6.0");
    }
}