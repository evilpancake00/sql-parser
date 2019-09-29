lexer grammar SqlLexer;

ALL:                                   'ALL';
AS:                                    'AS';
ASC:                                   'ASC';
AND:                                   'AND';
BETWEEN:                               'BETWEEN';
BY:                                    'BY';
DEFAULT:                               'DEFAULT';
DESC:                                  'DESC';
FROM:                                  'FROM';
FULL:                                  'FULL';
GROUP:                                 'GROUP';
IN:                                    'IN';
INNER:                                 'INNER';
JOIN:                                  'JOIN';
LEFT:                                  'LEFT';
LIKE:                                  'LIKE';
LIMIT:                                 'LIMIT';
NOT:                                   'NOT';
NULL:                                  'NULL';
OFFSET:                                'OFFSET';
ON:                                    'ON';
OR:                                    'OR';
ORDER:                                 'ORDER';
OUTER:                                 'OUTER';
RIGHT:                                 'RIGHT';
SELECT:                                'SELECT';
WHERE:                                 'WHERE';

DOUBLE_QUOTE_ID:    '"' ~'"'+ '"';
SQUARE_BRACKET_ID:  '[' ~']'+ ']';
LOCAL_ID:           '@' ([A-Z_$@#0-9] | FullWidthLetter)+;
DECIMAL:             DEC_DIGIT+;
ID:                  ( [A-Z_#] | FullWidthLetter) ( [A-Z_#$@0-9] | FullWidthLetter )*;
STRING:              'N'? '\'' (~'\'' | '\'\'')* '\'';
BINARY:              '0' 'X' HEX_DIGIT*;
FLOAT:               DEC_DOT_DEC;
REAL:                (DECIMAL | DEC_DOT_DEC) ('E' [+-]? DEC_DIGIT+);

EQUAL:               '=';

GREATER:             '>';
LESS:                '<';
EXCLAMATION:         '!';

DOT:                 '.';
LR_BRACKET:          '(';
RR_BRACKET:          ')';
COMMA:               ',';
SEMI:                ';';
STAR:                '*';
DOLLAR:              '$';
PLUS:                '+';
MINUS:               '-';

fragment DEC_DOT_DEC:  (DEC_DIGIT+ '.' DEC_DIGIT+ |  DEC_DIGIT+ '.' | '.' DEC_DIGIT+);
fragment HEX_DIGIT:    [0-9A-F];
fragment DEC_DIGIT:    [0-9];

fragment FullWidthLetter
    : '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u2c00'..'\u2fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3fff'
    | '\u4e00'..'\u9fff'
    | '\ua000'..'\ud7ff'
    | '\uf900'..'\ufaff'
    | '\uff00'..'\ufff0'
    // | '\u10000'..'\u1F9FF'  //not support four bytes chars
    // | '\u20000'..'\u2FA1F'
    ;