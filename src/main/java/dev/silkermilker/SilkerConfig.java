package dev.silkermilker;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SilkerConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("silker-milker.json");

    public boolean hudEnabled = true;
    public boolean showCoordinates = true;
    public boolean showFps = true;
    public boolean showAttackCooldown = true;
    public boolean showDirection = false;
    public boolean showArmorStatus = false;
    public boolean showAmethystHints = false;
    public boolean showContainerHints = false;
    public String accent = "#9D5CFF";
    public String text = "#EDE5F5";
    public String panel = "#990D0815";

    public static SilkerConfig load() {
        try {
            if (Files.exists(FILE)) {
                SilkerConfig loaded = net.minecraft.util.GsonHelper.GSON
                    .fromJson(Files.readString(FILE), SilkerConfig.class);
                if (loaded != null) return loaded;
            }
        } catch (Exception exception) {
            System.err.println("[Silker Milker] Could not read config: " + exception.getMessage());
        }
        SilkerConfig defaults = new SilkerConfig();
        defaults.save();
        return defaults;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, net.minecraft.util.GsonHelper.GSON.toJson(this));
        } catch (IOException exception) {
            System.err.println("[Silker Milker] Config save failed: " + exception.getMessage());
        }
    }

    public int accentColor() { return parse(accent, 0xFF9D5CFF); }
    public int textColor() { return parse(text, 0xFFEDE5F5); }
    public int panelColor() { return parse(panel, 0x990D0815); }

    private static int parse(String value, int fallback) {
        try {
            String hex = value.startsWith("#") ? value.substring(1) : value;
            long parsed = Long.parseLong(hex, 16);
            if (hex.length() == 6) parsed |= 0xFF000000L;
            return (int) parsed;
        } catch (RuntimeException exception) {
            return fallback;
        }
    }
}
