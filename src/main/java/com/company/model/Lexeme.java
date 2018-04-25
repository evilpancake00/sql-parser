package com.company.model;

import com.company.LexemeType;

public class Lexeme {

    private final LexemeType type;
    private final String text;

    public Lexeme(LexemeType type, String text) {
        this.type = type;
        this.text = text;
    }

    public LexemeType getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
