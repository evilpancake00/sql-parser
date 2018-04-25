package com.company.business;

import com.company.LexemeType;
import com.company.model.Lexeme;

import java.util.regex.Pattern;

public class LexicalAnalyzer {

    private final SyntacticalAnalyzer syntacticalAnalyzer;

    public LexicalAnalyzer(SyntacticalAnalyzer syntacticalAnalyzer) {
        this.syntacticalAnalyzer = syntacticalAnalyzer;
    }

    public void parse(String text) {
        StringBuilder lexeme = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char symbol = text.charAt(i);
            if (isTerminator(symbol)) {
                if (lexeme.length() > 0) {
                    parseLexeme(lexeme.toString());
                    if (!isSeparator(symbol)) {
                        parseLexeme(String.valueOf(symbol));
                    }
                    lexeme = new StringBuilder();
                }
            } else {
                lexeme.append(symbol);
            }
        }
    }

    private void parseLexeme(String lexeme) {
        syntacticalAnalyzer.parse(new Lexeme(getLexemeType(lexeme), lexeme));
    }

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[A-Za-z@$#_]+[A-Za-z0-9@$#_]*$");

    private LexemeType getLexemeType(String lexeme) {
        if (lexeme != null) {
            if (lexeme.equals("*")) {
                return LexemeType.STAR;
            }
            if (lexeme.equals(",")) {
                return LexemeType.COMMA;
            }
            if (lexeme.equals("(")) {
                return LexemeType.BRACKET_LEFT;
            }
            if (lexeme.equals(")")) {
                return LexemeType.BRACKET_RIGHT;
            }
            String upperCaseLexeme = lexeme.toUpperCase();
            if (upperCaseLexeme.equals("SELECT")) {
                return LexemeType.SELECT;
            }
            if (upperCaseLexeme.equals("FROM")) {
                return LexemeType.FROM;
            }
            if (upperCaseLexeme.equals("AS")) {
                return LexemeType.AS;
            }
            switch (upperCaseLexeme) {
                case "SUM":
                case "AVG":
                    return LexemeType.AGGREGATE;
            }
            if (IDENTIFIER_PATTERN.matcher(lexeme).matches()) {
                return LexemeType.IDENTIFIER;
            }
        }
        return null;
    }

    private boolean isTerminator(char symbol) {
        return isSeparator(symbol) || symbol == ',';
    }

    private boolean isSeparator(char symbol) {
        return symbol == ' ';
    }
}
