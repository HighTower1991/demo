package org.ly.demo.converter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Value {
    private static Pattern INPUT_LINE = Pattern.compile("((?:\\d+|\\?)(?:\\.\\d+)?)\\s(\\w+)", Pattern.UNICODE_CHARACTER_CLASS);
    final String name;
    final BigDecimal amount;

    Value(String name, BigDecimal amount) {
        this.name = name;
        this.amount = amount;
    }


    public static Value of(String inputLine) {
        Matcher matcher = INPUT_LINE.matcher(inputLine);
        if(!matcher.find()){
            throw new IllegalArgumentException("Incorrect input line: [" + inputLine + "]");
        }
        String amount = matcher.group(1);
        String name = matcher.group(2);
        return new Value(name, Objects.equals("?", amount) ? null : new BigDecimal(amount));
    }

    public boolean isUnknown() {
        return amount == null;
    }
}
