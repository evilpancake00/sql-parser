package com.company.business;

import com.company.LexemeType;
import com.company.exception.ParserException;
import com.company.model.Lexeme;

import java.util.regex.Pattern;

public class LexicalAnalyzer {

    private final SyntacticalAnalyzer syntacticalAnalyzer;

    public LexicalAnalyzer(SyntacticalAnalyzer syntacticalAnalyzer) {
        this.syntacticalAnalyzer = syntacticalAnalyzer;
    }

    public void parse(String text) throws ParserException {
        StringBuilder lexeme = new StringBuilder();
        int position = 0;
        while (position < text.length()) {
            char symbol = text.charAt(position);
            if (isTerminator(symbol)) {
                if (lexeme.length() > 0) {
                    parseLexeme(position, lexeme.toString());
                    if (!isSeparator(symbol)) {
                        parseLexeme(position + 1, String.valueOf(symbol));
                    }
                    lexeme = new StringBuilder();
                }
            } else {
                lexeme.append(symbol);
            }
            position++;
        }
        if (lexeme.length() > 0) {
            parseLexeme(position, lexeme.toString());
        }
        syntacticalAnalyzer.parse(new Lexeme(position, LexemeType.TERMINAL, null));
    }

    private void parseLexeme(Integer position, String lexeme) throws ParserException {
        syntacticalAnalyzer.parse(new Lexeme(position, getLexemeType(lexeme), lexeme));
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
