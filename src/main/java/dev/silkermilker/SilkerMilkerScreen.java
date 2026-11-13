package dev.silkermilker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Future-style Silker Milker ClickGUI.
 * Dark violet surfaces, compact sidebar, module cards and a settings pane.
 *
 * This screen only exposes client-side QoL settings. It does not implement
 * combat automation, hidden ESP, packet manipulation, or anti-cheat bypasses.
 */
public final class SilkerMilkerScreen extends Screen {
    private static final int BG = 0xF5090610;
    private static final int PANEL = 0xF5110B1D;
    private static final int PANEL_ALT = 0xFF1B1028;
    private static final int PANEL_HOVER = 0xFF29183B;
    private static final int LINE = 0xFF39234F;
    private static final int MUTED = 0xFFAA9ABF;
    private static final int WHITE = 0xFFF4EDFF;
    private static final int PURPLE = 0xFF9D5CFF;
    private static final int PURPLE_BRIGHT = 0xFFC18BFF;
    private static final int GREEN = 0xFF75E0B0;

    private static final String[] CATEGORIES = {
        "Overview", "Combat", "Visuals", "HUD", "Utility", "Cosmetics", "Settings"
    };

    private String category = "Overview";
    private String selectedModule = "HUD";
    private String searchText = "";
    private int accentIndex = 0;
    private boolean compactHud = true;
    private boolean coordinates = true;
    private boolean fps = true;
    private boolean potionHud = false;
    private boolean armorHud = false;
    private boolean waypointHints = false;

    private final List<Button> dynamicButtons = new ArrayList<>();

    private static final int[] ACCENTS = {
        0xFF9D5CFF, 0xFFB879FF, 0xFF7D54D9, 0xFF6C5CE7, 0xFFD18CFF
    };
    private static final String[] ACCENT_NAMES = {
        "Dark Violet", "Lavender", "Royal Purple", "Ultraviolet", "Orchid"
    };

    public SilkerMilkerScreen() {
        super(Component.literal("Silker Milker"));
        for (int i = 0; i < ACCENTS.length; i++) {
            if ((ACCENTS[i] & 0xFFFFFF) == (SilkerMilkerClient.config.accentColor() & 0xFFFFFF)) {
                accentIndex = i;
                break;
            }
        }
    }

    @Override
    protected void init() {
        rebuildControls();
    }

    private void rebuildControls() {
        clearWidgets();
        dynamicButtons.clear();

        int left = (width - 760) / 2;
        int top = (height - 450) / 2;
        if (left < 8) left = 8;
        if (top < 8) top = 8;

        // Sidebar navigation
        for (int i = 0; i < CATEGORIES.length; i++) {
            final String name = CATEGORIES[i];
            Button nav = Button.builder(Component.literal(name), b -> {
                category = name;
                rebuildWidgets();
            }).bounds(left + 16, top + 108 + i * 39, 132, 29).build();
            addRenderableWidget(nav);
        }

        // Accent control
        addRenderableWidget(Button.builder(
            Component.literal("ACCENT  ·  " + ACCENT_NAMES[accentIndex]),
            b -> cycleAccent()
        ).bounds(left + 190, top + 386, 180, 28).build());

        // Main pane controls based on current page
        if ("HUD".equals(category) || "Overview".equals(category)) {
            addToggle(left + 190, top + 176, "Coordinates", SilkerMilkerClient.config.showCoordinates, "Coordinates");
            addToggle(left + 190, top + 218, "FPS Counter", SilkerMilkerClient.config.showFps, "FPS");
            addToggle(left + 414, top + 176, "Direction HUD", SilkerMilkerClient.config.showDirection, "Direction");
            addToggle(left + 414, top + 218, "Armor Status", SilkerMilkerClient.config.showArmorStatus, "Armor");
        } else if ("Combat".equals(category)) {
            addToggle(left + 190, top + 176, "Attack Cooldown HUD", SilkerMilkerClient.config.showAttackCooldown, "AttackCooldown");
            addRenderableWidget(Button.builder(Component.literal("Combat stats: visual only"), b -> {})
                .bounds(left + 190, top + 224, 210, 28).build());
        } else if ("Visuals".equals(category)) {
            addToggle(left + 190, top + 176, "Amethyst Growth Hints", SilkerMilkerClient.config.showAmethystHints, "AmethystHints");
            addToggle(left + 414, top + 176, "Container Hints", SilkerMilkerClient.config.showContainerHints, "ContainerHints");
            addRenderableWidget(Button.builder(Component.literal("Cycle accent color"), b -> cycleAccent())
                .bounds(left + 190, top + 224, 180, 28).build());
        } else if ("Utility".equals(category)) {
            addToggle(left + 190, top + 176, "Waypoint Hints", waypointHints, "Waypoints");
        } else if ("Cosmetics".equals(category)) {
            addRenderableWidget(Button.builder(Component.literal("Hand animation: default"), b -> {
                b.setMessage(Component.literal("Hand animation: smooth (preview)"));
            }).bounds(left + 190, top + 176, 210, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Item model overrides: planned"), b -> {})
                .bounds(left + 190, top + 218, 210, 28).build());
        } else if ("Settings".equals(category)) {
            addRenderableWidget(Button.builder(Component.literal("Reset GUI layout"), b -> {
                category = "Overview";
                selectedModule = "HUD";
                rebuildWidgets();
            }).bounds(left + 190, top + 176, 180, 28).build());
        }

        addRenderableWidget(Button.builder(Component.literal("DONE"), b -> onClose())
            .bounds(left + 632, top + 404, 92, 28).build());
    }

    private void addToggle(int x, int y, String label, boolean enabled, String key) {
        String state = enabled ? "  ON" : "  OFF";
        Button button = Button.builder(Component.literal(label + state), b -> {
            switch (key) {
                case "HUD" -> compactHud = !compactHud;
                case "Coordinates" -> coordinates = !coordinates;
                case "FPS" -> fps = !fps;
                case "Armor" -> armorHud = !armorHud;
                case "Potions" -> potionHud = !potionHud;
                case "Waypoints" -> waypointHints = !waypointHints;
                case "Coordinates" -> SilkerMilkerClient.config.showCoordinates = !SilkerMilkerClient.config.showCoordinates;
                case "FPS" -> SilkerMilkerClient.config.showFps = !SilkerMilkerClient.config.showFps;
                case "AttackCooldown" -> SilkerMilkerClient.config.showAttackCooldown = !SilkerMilkerClient.config.showAttackCooldown;
                case "Direction" -> SilkerMilkerClient.config.showDirection = !SilkerMilkerClient.config.showDirection;
                case "Armor" -> SilkerMilkerClient.config.showArmorStatus = !SilkerMilkerClient.config.showArmorStatus;
                case "AmethystHints" -> SilkerMilkerClient.config.showAmethystHints = !SilkerMilkerClient.config.showAmethystHints;
                case "ContainerHints" -> SilkerMilkerClient.config.showContainerHints = !SilkerMilkerClient.config.showContainerHints;
                default -> { }
            }
            SilkerMilkerClient.config.save();
            selectedModule = label;
            rebuildWidgets();
        }).bounds(x, y, 190, 30).build();
        addRenderableWidget(button);
    }

    private void cycleAccent() {
        accentIndex = (accentIndex + 1) % ACCENTS.length;
        SilkerMilkerClient.config.accent =
            String.format("#%06X", ACCENTS[accentIndex] & 0xFFFFFF);
        SilkerMilkerClient.config.save();
        rebuildWidgets();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);

        int left = Math.max(8, (width - 760) / 2);
        int top = Math.max(8, (height - 450) / 2);
        int right = Math.min(width - 8, left + 760);
        int bottom = Math.min(height - 8, top + 450);
        int accent = accent();

        // Outer shell and subtle double-edge frame
        graphics.fill(0, 0, width, height, 0xB9000000);
        graphics.fill(left - 1, top - 1, right + 1, bottom + 1, 0xFF5A347D);
        graphics.fill(left, top, right, bottom, BG);

        // Header
        graphics.fill(left, top, right, top + 66, PANEL);
        graphics.fill(left, top + 65, right, top + 67, LINE);
        graphics.fill(left + 18, top + 18, left + 48, top + 48, accent);
        graphics.drawString(font, "S", left + 28, top + 26, 0xFFFFFFFF, true);
        graphics.drawString(font, "SILKER", left + 60, top + 17, WHITE, true);
        graphics.drawString(font, "MILKER", left + 60, top + 31, accent, true);
        graphics.drawString(font, "F U T U R E   C L I E N T", left + 178, top + 27, MUTED, false);
        graphics.drawString(font, "26.3  /  FABRIC", right - 112, top + 27, MUTED, false);

        // Sidebar
        graphics.fill(left, top + 67, left + 166, bottom, 0xFF0E0917);
        graphics.fill(left + 165, top + 67, left + 167, bottom, LINE);
        graphics.drawString(font, "NAVIGATION", left + 18, top + 84, MUTED, false);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = top + 108 + i * 39;
            boolean active = category.equals(CATEGORIES[i]);
            if (active) {
                graphics.fill(left + 12, y - 2, left + 151, y + 28, PANEL_HOVER);
                graphics.fill(left + 12, y - 2, left + 15, y + 28, accent);
            }
            graphics.drawString(font, iconFor(CATEGORIES[i]), left + 23, y + 8, active ? accent : MUTED, true);
            graphics.drawString(font, CATEGORIES[i], left + 43, y + 8, active ? WHITE : MUTED, active);
        }
        graphics.drawString(font, "BUILD  1.0.0", left + 18, bottom - 25, 0xFF776887, false);

        // Content area
        int contentLeft = left + 188;
        graphics.drawString(font, category.toUpperCase(), contentLeft, top + 88, WHITE, true);
        graphics.drawString(font, subtitle(), contentLeft, top + 104, MUTED, false);

        // Search-style visual field
        graphics.fill(contentLeft, top + 126, right - 20, top + 154, PANEL_ALT);
        graphics.drawString(font, "⌕   Search modules...", contentLeft + 10, top + 136, 0xFF8F7DA5, false);

        // Module cards
        if ("Overview".equals(category) || "HUD".equals(category)) {
            drawCard(graphics, contentLeft, top + 166, 204, 166, "HUD CORE", "Client information overlay", accent);
            drawCard(graphics, contentLeft + 218, top + 166, 204, 166, "STATUS PANELS", "Armor and potion readouts", accent);
        } else if ("Combat".equals(category)) {
            drawCard(graphics, contentLeft, top + 166, 426, 166, "COMBAT HUD", "Attack cooldown  ·  Manual-use information", accent);
        } else if ("Visuals".equals(category)) {
            drawCard(graphics, contentLeft, top + 166, 426, 166, "VISUAL TOOLS", "Amethyst growth hints  ·  Container hints", accent);
        } else if ("Cosmetics".equals(category)) {
            drawCard(graphics, contentLeft, top + 166, 426, 166, "COSMETICS", "Hand animation and client-side item appearance", accent);
        } else if ("Utility".equals(category)) {
            drawCard(graphics, contentLeft, top + 166, 426, 166, "CLIENT UTILITIES", "Local waypoints and helpful HUD indicators", accent);
        } else {
            drawCard(graphics, contentLeft, top + 166, 426, 166, "PREFERENCES", "Theme and interface configuration", accent);
        }

        // Footer status
        graphics.fill(contentLeft, bottom - 49, right - 20, bottom - 48, LINE);
        graphics.drawString(font, "●", contentLeft, bottom - 33, GREEN, true);
        graphics.drawString(font, "CLIENT READY", contentLeft + 12, bottom - 33, MUTED, false);
        graphics.drawString(font, "RIGHT SHIFT  ·  OPEN/CLOSE", right - 190, bottom - 33, 0xFF776887, false);

        super.render(graphics, mouseX, mouseY, delta);
    }

    private void drawCard(GuiGraphics graphics, int x, int y, int w, int h,
                          String title, String description, int accent) {
        graphics.fill(x, y, x + w, y + h, PANEL);
        graphics.fill(x, y, x + w, y + 2, accent);
        graphics.fill(x + 12, y + 14, x + 34, y + 36, PANEL_HOVER);
        graphics.drawString(font, "✦", x + 19, y + 21, accent, true);
        graphics.drawString(font, title, x + 12, y + 48, WHITE, true);
        graphics.drawString(font, description, x + 12, y + 64, MUTED, false);
        graphics.fill(x + 12, y + h - 30, x + w - 12, y + h - 29, LINE);
        graphics.drawString(font, "CONFIGURABLE", x + 12, y + h - 19, 0xFF8C79A5, false);
    }

    private String subtitle() {
        return switch (category) {
            case "Overview" -> "Your client, your setup.";
            case "Combat" -> "Combat information and manual-use helpers.";
            case "Visuals" -> "World hints and visual preferences.";
            case "HUD" -> "Useful information, without clutter.";
            case "Utility" -> "Small tools for a smoother session.";
            case "Cosmetics" -> "Client-side appearance and animations.";
            default -> "Personalize the Silker Milker experience.";
        };
    }

    private String iconFor(String name) {
        return switch (name) {
            case "Overview" -> "◈";
            case "Combat" -> "✚";
            case "Visuals" -> "✧";
            case "HUD" -> "▣";
            case "Utility" -> "⌁";
            case "Cosmetics" -> "✦";
            default -> "⚙";
        };
    }

    private int accent() {
        return SilkerMilkerClient.config.accentColor();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
