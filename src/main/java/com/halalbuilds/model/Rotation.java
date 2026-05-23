package com.halalbuilds.model;

import java.util.Arrays;

public enum Rotation {
    DEG_0(0),
    DEG_90(90),
    DEG_180(180),
    DEG_270(270);

    private final int degrees;

    Rotation(int degrees) {
        this.degrees = degrees;
    }

    public int degrees() {
        return degrees;
    }

    public static Rotation fromDegrees(int degrees) {
        return Arrays.stream(values())
            .filter(value -> value.degrees == degrees)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported rotation: " + degrees));
    }
}

