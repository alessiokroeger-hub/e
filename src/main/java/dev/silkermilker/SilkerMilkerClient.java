package dev.silkermilker;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class SilkerMilkerClient implements ClientModInitializer {
    public static final String MOD_ID = "silker_milker";

    private static final KeyMapping.Category CATEGORY =
        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"));

    private static final KeyMapping TOGGLE_HUD = KeyMappingHelper.registerKeyMapping(
        new KeyMapping("key.silker_milker.toggle_hud", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H, CATEGORY)
    );

    private static final KeyMapping OPEN_GUI = KeyMappingHelper.registerKeyMapping(
        new KeyMapping("key.silker_milker.open_gui", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT, CATEGORY)
    );

    public static boolean hudEnabled = true;
    public static SilkerConfig config;

    @Override
    public void onInitializeClient() {
        config = SilkerConfig.load();
        hudEnabled = config.hudEnabled;

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(MOD_ID, "main_hud"),
            SilkerMilkerClient::renderHud
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TOGGLE_HUD.consumeClick()) {
                hudEnabled = !hudEnabled;
                config.hudEnabled = hudEnabled;
                config.save();
                if (client.player != null) {
                    client.player.displayClientMessage(
                        Component.literal("§5Silker Milker §7HUD: " + (hudEnabled ? "§aON" : "§cOFF")),
                        true
                    );
                }
            }

            while (OPEN_GUI.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new SilkerMilkerScreen());
                } else if (client.screen instanceof SilkerMilkerScreen) {
                    client.setScreen(null);
                }
            }
        });
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (!hudEnabled || client.player == null || client.level == null || client.options.hideGui) return;

        int x = 8, y = 8;
        int rows = 0;
        if (config.showCoordinates) rows++;
        if (config.showFps) rows++;
        if (config.showAttackCooldown) rows++;
        if (config.showDirection) rows++;
        if (config.showArmorStatus) rows++;
        int height = 24 + rows * 14;

        graphics.fill(x - 5, y - 5, x + 190, y + height, config.panelColor());
        graphics.fill(x - 5, y - 5, x + 190, y - 3, config.accentColor());
        graphics.text(client.font, "SILKER MILKER", x, y, config.accentColor(), true);

        int lineY = y + 16;
        if (config.showCoordinates) {
            graphics.text(client.font,
                "XYZ: " + client.player.blockPosition().getX() + " / " +
                    client.player.blockPosition().getY() + " / " +
                    client.player.blockPosition().getZ(),
                x, lineY, config.textColor(), true);
            lineY += 14;
        }
        if (config.showFps) {
            graphics.text(client.font, "FPS: " + client.getFps(), x, lineY, config.textColor(), true);
            lineY += 14;
        }
        if (config.showAttackCooldown) {
            int percent = Math.round(client.player.getAttackStrengthScale(0.0F) * 100.0F);
            graphics.text(client.font, "ATTACK: " + percent + "%", x, lineY, config.textColor(), true);
            lineY += 14;
        }
        if (config.showDirection) {
            graphics.text(client.font, "FACING: " + client.player.getDirection().getName().toUpperCase(),
                x, lineY, config.textColor(), true);
            lineY += 14;
        }
        if (config.showArmorStatus) {
            int damaged = 0;
            for (var stack : client.player.getArmorSlots()) {
                if (!stack.isEmpty() && stack.isDamageableItem()) damaged += stack.getDamageValue();
            }
            graphics.text(client.font, "ARMOR DAMAGE: " + damaged, x, lineY, config.textColor(), true);
        }
    }
}
