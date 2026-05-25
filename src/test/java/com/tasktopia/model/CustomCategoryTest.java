package com.tasktopia.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomCategoryTest {

    @Test
    void constructorSetsNameAndColour() {
        CustomCategory cat = new CustomCategory("Work", "#FF5733");

        assertEquals("Work", cat.getName());
        assertEquals("#FF5733", cat.getColour());
    }

    @Test
    void settersUpdateFields() {
        CustomCategory cat = new CustomCategory("Old", "#000000");

        cat.setName("New");
        cat.setColour("#FFFFFF");

        assertEquals("New", cat.getName());
        assertEquals("#FFFFFF", cat.getColour());
    }

    @Test
    void getKeyReturnsLowercaseTrimmedName() {
        CustomCategory cat = new CustomCategory("  Travel  ", "#123456");
        assertEquals("travel", cat.getKey());
    }

    @Test
    void getKeyHandlesMultiWordNames() {
        CustomCategory cat = new CustomCategory("My Hobbies", "#FF0000");
        assertEquals("my hobbies", cat.getKey());
    }

    @Test
    void toStringFormatsCorrectly() {
        CustomCategory cat = new CustomCategory("Work", "#FF5733");
        assertEquals("Work (#FF5733)", cat.toString());
    }
}