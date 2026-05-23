package com.halalbuilds.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NameValidatorTest {
    @Test
    void acceptsSafeBuildNames() {
        assertEquals("house", NameValidator.validateBuildName("house"));
        assertEquals("small_house", NameValidator.validateBuildName("small_house"));
        assertEquals("castle-01", NameValidator.validateBuildName("castle-01"));
        assertEquals("Build123", NameValidator.validateBuildName("Build123"));
    }

    @Test
    void rejectsUnsafeBuildNames() {
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateBuildName("../house"));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateBuildName("house/roof"));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateBuildName("house\\roof"));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateBuildName("."));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateBuildName(".."));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateBuildName(""));
    }

    @Test
    void rejectsUnsafeImportNames() {
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateImportFileName("../server.properties"));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateImportFileName("C:\\temp\\castle.schem"));
        assertThrows(IllegalArgumentException.class, () -> NameValidator.validateImportFileName("castle/roof.schem"));
    }
}

