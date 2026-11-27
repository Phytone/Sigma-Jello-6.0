package com.phytone.sigma6.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.FontDescription;
import java.util.ArrayList;
import java.util.List;


import com.phytone.sigma6.client.gui.components.MenuButton;

public class MainMenuScreen extends TitleScreen {

    private final List<MenuButton> buttons = new ArrayList<>();

    private static Identifier background;

    private static final int BACKGROUND_WIDTH = 2048;
    private static final int BACKGROUND_HEIGHT = 982;

    private Component customText(String text, String fontName) {
        return Component.literal(text).withStyle(style ->
                style.withFont(
                        new FontDescription.Resource(
                                Identifier.fromNamespaceAndPath(
                                        "fonts", fontName
                                )
                        )
                )
        );
    }

    private void drawBrightText(
            GuiGraphicsExtractor graphics,
            Component text,
            int x,
            int y) {

        for (int i = 0; i < 5; i++) {
            graphics.text(
                    this.font,
                    text,
                    x, y,
                    0xFFFFFFFF,
                    false
            );
        }
    }

    public MainMenuScreen() {
        super();
    }


    private void updateBackground() {
        if (background != null) {
            return;
        }

        background = SVGLoader.register(
                "main_menu_background",
                "/assets/background/background.svg",
                BACKGROUND_WIDTH,
                BACKGROUND_HEIGHT
        );
    }

    @Override
    protected void init() {

        updateBackground();

        buttons.clear();

        int buttonSize = 38;
        int spacing = 10;

        int totalWidth = buttonSize * 5 + spacing * 4;

        int x = (this.width - totalWidth) / 2;
        int y = this.height / 2 + 10;

        int step = buttonSize + spacing;

        buttons.add(new MenuButton(
                "Singleplayer",
                "singleplayer",
                x, y, buttonSize,
                () -> minecraft.gui.setScreen(
                        new SelectWorldScreen(this)
                )
        ));

        buttons.add(new MenuButton(
                "Multiplayer",
                "multiplayer",
                x + step, y, buttonSize,
                () -> {
                    if (minecraft.allowsMultiplayer()) {
                        minecraft.gui.setScreen(
                                minecraft.options.skipMultiplayerWarning
                                        ? new JoinMultiplayerScreen(this)
                                        : new SafetyScreen(this)
                        );
                    }
                }
        ));

        buttons.add(new MenuButton(
                "Alt Manager",
                "altmanager",
                x + step * 2, y, buttonSize,
                () -> {
                }
        ));

        buttons.add(new MenuButton(
                "Settings",
                "settings",
                x + step * 3, y, buttonSize,
                () -> minecraft.gui.setScreen(
                        new OptionsScreen(this, minecraft.options)
                )
        ));

        buttons.add(new MenuButton(
                "Quit Game",
                "quit",
                x + step * 4, y, buttonSize,
                () -> minecraft.stop()
        ));
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float a) {

        if (background == null) {
            return;
        }

        float scale = Math.max(
                (float) this.width / BACKGROUND_WIDTH,
                (float) this.height / BACKGROUND_HEIGHT
        );

        int renderWidth = (int) Math.ceil(BACKGROUND_WIDTH * scale);
        int renderHeight = (int) Math.ceil(BACKGROUND_HEIGHT * scale);

        int x = (this.width - renderWidth) / 2;
        int y = (this.height - renderHeight) / 2;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                background,
                x, y,
                0.0F, 0.0F,
                renderWidth, renderHeight,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float a) {

        extractBackground(graphics, mouseX, mouseY, a);

        Component logo = customText("Jello", "logo");

        graphics.text(
                this.font,
                logo,
                (this.width - this.font.width(logo)) / 2,
                this.height / 2 - 25,
                0xFFFFFFFF,
                false
        );

        for (MenuButton button : buttons) {
            button.render(graphics, mouseX, mouseY);
        }

        drawBrightText(
                graphics,
                customText("Changelog", "footer"),
                8, 6
        );

        Component username = customText(
                minecraft.getUser().getName(),
                "footer"
        );

        drawBrightText(
                graphics,
                username,
                this.width - this.font.width(username) - 8,
                6
        );

        drawBrightText(
                graphics,
                customText("© Phytone", "footer"),
                4,
                this.height - 12
        );

        Component version = customText(
                "Sigma Jello 6.0",
                "footer"
        );

        drawBrightText(
                graphics,
                version,
                this.width - this.font.width(version) - 4,
                this.height - 12
        );
    }
    @Override
    public boolean mouseClicked(
            MouseButtonEvent event,
            boolean doubleClick) {

        if (event.button() == 1) {
            for (MenuButton button : buttons) {
                if (button.click(event.x(), event.y())) {
                    return true;
                }
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}