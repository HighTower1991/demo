package org.ly.demo.converter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public class Converter {
    private final List<Conversation> knowledge;
    final Map<String, List<Conversation>> index = new HashMap<>();

    public Converter(List<Conversation> knowledge) {
        this.knowledge = knowledge;
        for (Conversation currentLine : knowledge) {

            Value leftOperand = currentLine.getLeftOperand();
            Value rightOperand = currentLine.getRightOperand();

            List<Conversation> lcs = index.computeIfAbsent(leftOperand.name, k -> new ArrayList<>());
            List<Conversation> rcs = index.computeIfAbsent(rightOperand.name, k -> new ArrayList<>());

            for (Conversation lc : lcs) {
                List<Conversation.Route> lInheritedRoutes =  lc.getRoutes(leftOperand.name);
                currentLine.putRoutes(lInheritedRoutes);
                lc.assignRoute(rightOperand, currentLine);
            }
            lcs.add(currentLine);

            for (Conversation rc : rcs) {
                List<Conversation.Route> rInheritedRoutes =  rc.getRoutes(leftOperand.name);
                currentLine.putRoutes(rInheritedRoutes);
                rc.assignRoute(leftOperand, currentLine);
            }
            rcs.add(currentLine);
        }
    }

    public Value convert(Value source, Value target) {
        if (source.isUnknown() || !target.isUnknown()){
            throw new IllegalStateException();
        }
        Conversation conversationPath = findShortestPath(source,target);
        if (conversationPath == null){
            return target;
        }
        Value converted = conversationPath.calculate(source, target);
        final BigDecimal round = round(converted);
        return new Value(converted.name, round);
    }

    private BigDecimal round(Value converted) {
        return converted.amount.setScale(2, RoundingMode.FLOOR);
    }

    Conversation findShortestPath(Value source, Value target) {
        List<Conversation> conversations = index.get(source.name);
        if (conversations == null){
            return null;
        }
        Conversation conversation = conversations.stream()
                .filter(c -> c.countHops(target) >= 0)
                .min(Comparator.comparingInt(c -> c.countHops(target))).orElse(null);
        return conversation;
    }
}
