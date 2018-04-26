package com.company.exception;

import com.company.LexemeType;

import java.util.Set;

public class ParserException extends Exception {

    private final int position;
    private final Set<LexemeType> expected;

    public ParserException(Integer position, Set<LexemeType> expected) {
        this.position = position;
        this.expected = expected;
    }

    public int getPosition() {
        return position;
    }

    public Set<LexemeType> getExpected() {
        return expected;
    }
}
