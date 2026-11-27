package com.phytone.sigma6.mixin.client;

import com.phytone.sigma6.client.gui.MainMenuScreen;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(Gui.class)
public class MainMenu {

    @ModifyVariable(
            method = "setScreen",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Screen sigma$replaceTitleScreen(Screen screen) {
        if (screen instanceof TitleScreen
                && !(screen instanceof MainMenuScreen)) {
            return new MainMenuScreen();
        }

        return screen;
    }

    @ModifyExpressionValue(
            method = "setScreen",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/gui/screens/TitleScreen"
            )
    )
    private TitleScreen sigma$replaceInternalTitleScreen(
            TitleScreen original) {
        return new MainMenuScreen();
    }
}