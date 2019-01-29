package org.ly.demo.converter;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ConversationTest {
    /**
     * Для преобразования строк из файла воспользуемся фабрикой.
     * Формат: a V = b W
     */
    @Test
    public void whenCorrectStringThenCreateConversationInstance() {
        Conversation simpleConversation = Conversation.of("10.01 кг = 100 л");
        assertEquals(new BigDecimal("10.01"), simpleConversation.getLeftOperand().amount);
        assertEquals("кг", simpleConversation.getLeftOperand().name);
        assertEquals(new BigDecimal("100"), simpleConversation.getRightOperand().amount);
        assertEquals("л", simpleConversation.getRightOperand().name);
    }

}