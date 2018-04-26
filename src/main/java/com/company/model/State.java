package com.company.model;

import com.company.LexemeType;

import java.util.*;

import static java.util.Collections.singletonList;

public class State {

    private final Map<LexemeType, Collection<State>> transitions = new HashMap<>();
    private final boolean terminal;

    public State() {
        terminal = false;
    }

    public State(boolean terminal) {
        this.terminal = terminal;
    }

    public Collection<State> parse(Lexeme lexeme) {
        return transitions.getOrDefault(lexeme.getType(), new ArrayList<>());
    }

    public void on(LexemeType lexemeType, State next) {
        transition(lexemeType, next);
    }

    public State on(LexemeType lexemeType) {
        return transition(lexemeType, new State());
    }

    private State transition(LexemeType lexemeType, State next) {
        if (!transitions.containsKey(lexemeType)) {
            transitions.put(lexemeType, new ArrayList<>());
        }
        transitions.get(lexemeType).add(next);
        return next;
    }

    public void end() {
        transitions.put(LexemeType.TERMINAL, singletonList(new State(true)));
    }

    public boolean isTerminal() {
        return terminal;
    }

    public Set<LexemeType> getExpected() {
        return transitions.keySet();
    }
}
