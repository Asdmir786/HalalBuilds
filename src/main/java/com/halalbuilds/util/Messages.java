package com.halalbuilds.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Messages {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private Messages() {
    }

    public static Component prefixed(String prefix, String message) {
        return LEGACY.deserialize(prefix + message);
    }
}

