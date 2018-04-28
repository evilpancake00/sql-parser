package com.company.business;

import com.company.LexemeType;
import com.company.exception.ParserException;
import com.company.model.Lexeme;
import com.company.model.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static com.company.LexemeType.*;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

public class SyntacticalAnalyzer {

    private Collection<State> currentStates;

    public SyntacticalAnalyzer() {
        State root = new State();
        State select = new State();
        State from = new State();
        State identifier = new State();
        State alias = new State();
        State table = new State();
        State join = new State();
        State condition = new State();
        State operation = new State();
        State junction = new State();

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
        from.on(IDENTIFIER)
            .on(IDENTIFIER, table);

        table
            .end();
        table
            .on(COMMA, from);

        table
            .on(JOIN, join);
        table
            .on(INNER)
            .on(JOIN, join);
        table
            .on(LEFT)
            .on(JOIN, join);
        table
            .on(RIGHT)
            .on(JOIN, join);

        join.on(IDENTIFIER, condition);
        join.on(IDENTIFIER)
            .on(IDENTIFIER, condition);

        condition
            .on(ON).on(IDENTIFIER, operation);

        operation
            .on(EQ).on(IDENTIFIER, junction);
        operation
            .on(NE).on(IDENTIFIER, junction);
        operation
            .on(GT).on(IDENTIFIER, junction);
        operation
            .on(LT).on(IDENTIFIER, junction);
        operation
            .on(GE).on(IDENTIFIER, junction);
        operation
            .on(LE).on(IDENTIFIER, junction);

        junction
            .end();

        junction
            .on(AND, operation);
        junction
            .on(OR, operation);

        junction
            .on(JOIN, join);
        junction
            .on(INNER)
            .on(JOIN, join);
        junction
            .on(LEFT)
            .on(JOIN, join);
        junction
            .on(RIGHT)
            .on(JOIN, join);

        currentStates = singletonList(root);
    }

    public void parse(Lexeme lexeme) throws ParserException {
        Collection<State> nextStates = new ArrayList<>();
        for (State state : currentStates) {
            Collection<State> states = state.parse(lexeme);
            nextStates.addAll(states);
        }
        if (lexeme.getType() == LexemeType.TERMINAL && nextStates.stream().noneMatch(State::isTerminal)) {
            throw new ParserException(lexeme.getPosition(), getExpected(nextStates));
        }
        currentStates = nextStates;
    }

    private static Set<LexemeType> getExpected(Collection<State> states) {
        return states.stream().flatMap(state -> state.getExpected().stream()).collect(toSet());
    }
}
