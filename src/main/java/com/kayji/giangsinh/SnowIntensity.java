package com.kayji.giangsinh;

import java.util.Locale;

public enum SnowIntensity {
    OFF("off"),
    LIGHT("nhe"),
    MEDIUM("vua"),
    HEAVY("day");

    private final String key;

    SnowIntensity(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static SnowIntensity fromString(String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "off", "tat", "none" -> OFF;
            case "nhe", "light", "low" -> LIGHT;
            case "vua", "medium", "vua-vua" -> MEDIUM;
            case "day", "heavy", "cao", "thick" -> HEAVY;
            default -> null;
        };
    }
}
