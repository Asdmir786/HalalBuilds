package com.halalbuilds.util;

import java.util.regex.Pattern;

public final class NameValidator {
    private static final int MAX_NAME_LENGTH = 48;
    private static final Pattern SAFE_NAME = Pattern.compile("^[A-Za-z0-9_-]+$");

    private NameValidator() {
    }

    public static String validateBuildName(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Build name is required.");
        }
        String normalized = input.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Build name cannot be empty.");
        }
        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Build name is too long. Max " + MAX_NAME_LENGTH + " characters.");
        }
        if (".".equals(normalized) || "..".equals(normalized)) {
            throw new IllegalArgumentException("Build name is not allowed.");
        }
        if (!SAFE_NAME.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Build name may only contain letters, numbers, '_' and '-'.");
        }
        return normalized;
    }

    public static String validateImportFileName(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Filename is required.");
        }
        String normalized = input.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty.");
        }
        if (normalized.contains("/") || normalized.contains("\\") || normalized.contains("..") || normalized.contains(":")) {
            throw new IllegalArgumentException("Filename must stay inside the HalalBuilds import folder.");
        }
        return normalized;
    }
}

