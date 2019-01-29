package org.ly.demo.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Conversation {
    private static Pattern INPUT_LINE = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s(\\w+)\\s\\=\\s(\\d+(?:\\.\\d+)?)\\s(\\w+)", Pattern.UNICODE_CHARACTER_CLASS);

    private final Value left;
    private final Value right;

    final List<Route> routes = new ArrayList<>();

    private Conversation(BigDecimal a, String v, BigDecimal b, String w) {
        left = new Value(v, a);
        right = new Value(w, b);
        initRoutes();
    }

    private void initRoutes() {
        routes.add(new Route(left.name, this, 0));
        routes.add(new Route(right.name, this, 0));
    }

    static Conversation of(String inputLine) {
        Matcher matcher = INPUT_LINE.matcher(inputLine);
        if(!matcher.find()){
            throw new IllegalArgumentException("Incorrect input line: [" + inputLine + "]");
        }
        String a = matcher.group(1);
        String v = matcher.group(2);
        String b = matcher.group(3);
        String w = matcher.group(4);
        return new Conversation(
                new BigDecimal(a), v,
                new BigDecimal(b), w
        );
    }

    Value calculate(Value source, Value target) {
        final Route route = routes.stream().filter(r -> r.to.equals(target.name)).min(Comparator.comparingInt(r2 -> r2.hops)).get();
        final Value factor = getFactor(source);
        final Value reducedPathConversion = new Value(factor.name, source.amount.multiply(factor.amount));
        if (route.hops == 0){
            return reducedPathConversion;
        } else{
            return route.conversation.calculate(reducedPathConversion, target);
        }
    }

    Value getLeftOperand(){
        return left;
    }

    Value getRightOperand(){
        return right;
    }

    int countHops(Value target) {
        return routes.stream().filter(r -> r.to.equals(target.name)).map(r -> r.hops).min(Integer::compareTo).orElse(Integer.MIN_VALUE);
    }

    void assignRoute(Value operand, Conversation currentLine) {
        routes.add(new Route(operand.name, currentLine, 1));
    }

    void putRoutes(List<Route> routes){
        this.routes.addAll(routes);
    }

    private Value getFactor(Value operand) {
        if(left.name.equals(operand.name)){
            return new Value(right.name, right.amount.divide(left.amount,6, RoundingMode.HALF_EVEN));
        } else if(right.name.equals(operand.name)){
            return new Value(left.name, left.amount.divide(right.amount, 6, RoundingMode.HALF_EVEN));
        } else {
            throw new IllegalStateException();
        }
    }

    List<Route> getRoutes(String from) {
        return routes.stream()
                .filter(r -> !from.equals(r.to))
                .map(r -> new Route(r.to, this, r.hops+1))
                .collect(Collectors.toList());
    }

    static class Route{
        final String to;
        final Conversation conversation;
        final int hops;

        public Route(String to, Conversation conversation, int hops) {
            this.to = to;
            this.conversation = conversation;
            this.hops = hops;
        }
    }

}
