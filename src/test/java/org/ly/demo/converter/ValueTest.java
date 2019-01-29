package org.ly.demo.converter;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ValueTest {

    @Test
    public void whenSimpleCreateByInputThenInstanceCreate() {
        Value v = Value.of("10.01 кг");
        assertEquals("кг", v.name);
        assertEquals(new BigDecimal("10.01"), v.amount);
    }

    @Test
    public void whenLineContainsXCharacterThenCreateValueWithUnknowAmmount() {
        Value v = Value.of("? л");
        assertEquals("л", v.name);
        assertNull(null, v.amount);
        assertTrue(v.isUnknown());
    }
}