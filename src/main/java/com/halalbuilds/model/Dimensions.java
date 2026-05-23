package com.halalbuilds.model;

public record Dimensions(int width, int height, int length) {
    public long volume() {
        return (long) width * (long) height * (long) length;
    }
}

