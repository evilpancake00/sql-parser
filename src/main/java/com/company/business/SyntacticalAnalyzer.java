package com.company.business;

import com.company.exception.ParserException;
import com.company.model.Lexeme;
import com.company.model.State;

import java.util.ArrayList;
import java.util.Collection;

import static com.company.LexemeType.*;
import static java.util.Collections.singletonList;

public class SyntacticalAnalyzer {

    private Collection<State> currentStates;

    public SyntacticalAnalyzer() {
        State root = new State();
        State select = new State();
        State from = new State();
        State identifier = new State();
        State alias = new State();
        State table = new State();

        root.on(SELECT, select);

        select
            .on(IDENTIFIER, identifier);
        select
            .on(STAR)
            .on(FROM, from);
        select
            .on(AGGREGATE)
            .on(BRACKET_LEFT)
            .on(IDENTIFIER)
            .on(BRACKET_RIGHT)
            .on(FROM, from);

        identifier
            .on(COMMA)
            .on(IDENTIFIER, identifier);
        identifier
            .on(FROM, from);
        identifier
            .on(AS)
            .on(IDENTIFIER, alias);

        alias
            .on(COMMA, select);
        alias
            .on(FROM, from);

        from.on(IDENTIFIER, table);

        table
            .end();
        table
            .on(COMMA)
            .on(IDENTIFIER, table);

        currentStates = singletonList(root);
    }

    public void parse(Lexeme lexeme) throws ParserException {
        Collection<State> nextStates = new ArrayList<>();
        for (State state : currentStates) {
            Collection<State> states = state.parse(lexeme);
            if (states.isEmpty()) {
                throw new ParserException(lexeme.getPosition(), state.getExpected());
            }
            nextStates.addAll(states);
        }
        currentStates = nextStates;
    }
}
