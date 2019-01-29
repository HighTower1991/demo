package org.ly.demo.converter;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ConverterTest {

    /**
     * Банальная проверка простого преобразования
     */
    @Test
    public void whenSimpleConversationThenCorrectValue() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("10.00 кг = 100 л")
        );
        Converter tested = new Converter(knowledge);
        Value result = tested.convert(Value.of("5.00 кг"), Value.of("? л"));
        assertTrue(new BigDecimal("50").compareTo(result.amount) == 0);
        assertEquals("л", result.name);
    }

    @Test
    public void checkSimpleRoutes() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("10.00 кг = 100 л")
        );
        Converter tested = new Converter(knowledge);
        tested.index.containsKey("кг");
        tested.index.containsKey("л");
        final Conversation kg = tested.index.get("кг").get(0);
        assertTrue("Каждая конверсия в роутах содержит роуты сама на себя с 0 числом хопов."
                , kg.routes.stream().filter(r -> r.to.equals("кг") && r.hops == 0 && r.conversation == kg).findAny().isPresent());
        assertTrue("Каждая конверсия в роутах содержит роуты сама на себя с 0 числом хопов."
                , kg.routes.stream().filter(r -> r.to.equals("л") && r.hops == 0 && r.conversation == kg).findAny().isPresent());
        assertNotNull("Каждая конверсия должна иметь путь внутри своих операндов"
                , tested.findShortestPath(Value.of("5.00 кг"), Value.of("? л")));

        assertNull("Корректная обработка невозможности конверсии", tested.findShortestPath(Value.of("5.00 см"), Value.of("? л")));
    }


    /**
     * Последовательные преобразования
     */
    @Test
    public void simpleSequentualConversation() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("10.00 кг = 100 л"),
                Conversation.of("10 л = 2 руб")
        );
        Converter tested = new Converter(knowledge);
        Value result = tested.convert(Value.of("5.00 кг"), Value.of("? руб"));
        assertTrue(new BigDecimal("10").compareTo(result.amount) == 0);
        assertEquals("руб", result.name);
    }

    @Test
    public void checkMoreComplexRouting() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("10.00 кг = 100 л"),
                Conversation.of("10 л = 2 руб")
        );
        Converter tested = new Converter(knowledge);
        Conversation rub = tested.index.get("руб").get(0);
        Conversation kg = tested.index.get("кг").get(0);
        assertTrue("Каждая конверсия в роутах содержит роуты на других за большее чем 0 хопов"
                , rub.routes.stream().filter(r -> r.to.equals("кг") && r.hops == 1 && r.conversation == kg).findAny().isPresent());
    }

    @Test
    public void severalItemInConversionPath() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("10.00 кг = 100 л"),
                Conversation.of("10 л = 2 руб"),
                Conversation.of("1 руб = 20 см")
        );
        Converter tested = new Converter(knowledge);
        Value result = tested.convert(Value.of("40 см"), Value.of("? кг"));
        assertTrue(new BigDecimal("1").compareTo(result.amount) == 0);
        assertEquals("кг", result.name);
    }

    @Test
    public void cycleInConversionPath() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("10.00 кг = 100 л"),
                Conversation.of("10 л = 2 руб"),
                Conversation.of("1 руб = 20 см"),
                Conversation.of("40 см = 1 кг"),
                Conversation.of("20 руб = 1 пир")
        );
        Converter tested = new Converter(knowledge);
        Value result = tested.convert(Value.of("3 пир"), Value.of("? кг"));
        assertTrue(new BigDecimal("30").compareTo(result.amount) == 0);
        assertEquals("кг", result.name);
    }

    @Test
    public void prepareAcceptanceRoundTrouble() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("2 bar = 12 ring"),
                Conversation.of("16.8 ring = 2 pyramid"));
        Converter tested = new Converter(knowledge);
        final Value convert = tested.convert(Value.of("1 pyramid"), Value.of("? bar"));
        final BigDecimal amount = convert.amount;
        assertTrue("" + amount, new BigDecimal("1.4").compareTo(amount) == 0);
    }

    @Test
    public void acceptanceTest() {
        List<Conversation> knowledge = Arrays.asList(
                Conversation.of("1024 byte = 1 kilobyte"),
                Conversation.of("2 bar = 12 ring"),
                Conversation.of("16.8 ring = 2 pyramid"),
                Conversation.of("4 hare = 1 cat"),
                Conversation.of("5 cat = 0.5 giraffe"),
                Conversation.of("1 byte = 8 bit"),
                Conversation.of("15 ring = 2.5 bar"));
        Converter tested = new Converter(knowledge);
        assertTrue(new BigDecimal("1.4").compareTo(tested.convert(Value.of("1 pyramid"), Value.of("? bar")).amount) == 0);
        assertTrue(new BigDecimal("40").compareTo(tested.convert(Value.of("1 giraffe"), Value.of("? hare")).amount) == 0);
        assertNull(tested.convert(Value.of("0.5 byte"), Value.of("? cat")).amount);
        assertTrue(new BigDecimal("16384").compareTo(tested.convert(Value.of("2 kilobyte"), Value.of("? bit")).amount) == 0);
    }
}