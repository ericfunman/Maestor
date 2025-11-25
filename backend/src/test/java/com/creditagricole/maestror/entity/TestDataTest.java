package com.creditagricole.maestror.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDataTest {

    @Test
    void testTestDataBuilder() {
        TestData testData = TestData.builder()
            .textData("Test text")
            .build();

        assertNotNull(testData);
        assertEquals("Test text", testData.getTextData());
    }

    @Test
    void testTestDataSettersGetters() {
        TestData testData = new TestData();
        testData.setId(1L);
        testData.setTextData("Sample data");

        assertEquals(1L, testData.getId());
        assertEquals("Sample data", testData.getTextData());
    }

    @Test
    void testTextDataMaxLength() {
        String longText = "a".repeat(500);
        TestData testData = TestData.builder()
            .textData(longText)
            .build();

        assertEquals(500, testData.getTextData().length());
    }
}
