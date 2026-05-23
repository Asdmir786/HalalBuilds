package com.halalbuilds.model;

public enum PasteMode {
    SMART_FOUNDATION,
    EXACT;

    public static PasteMode fromConfigValue(String value) {
        if (value == null) {
            return SMART_FOUNDATION;
        }
        return switch (value.trim().toLowerCase()) {
            case "exact" -> EXACT;
            case "smart_foundation", "smart-foundation", "smartfoundation" -> SMART_FOUNDATION;
            default -> SMART_FOUNDATION;
        };
    }
}

