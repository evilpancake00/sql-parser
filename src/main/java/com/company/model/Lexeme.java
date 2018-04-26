package com.company.model;

import com.company.LexemeType;

public class Lexeme {

    private final int position;
    private final LexemeType type;
    private final String text;

    public Lexeme(int position, LexemeType type, String text) {
        this.position = position;
        this.type = type;
        this.text = text;
    }

    public int getPosition() {
        return position;
    }

    public LexemeType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Lexeme { position: '" + position + "', type: '" + type + "', text: '" + text + "' }";
    }
}
