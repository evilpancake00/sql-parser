parser grammar SqlParser;

options { tokenVocab=SqlLexer; }

select_statement
    : query_expression order_by_clause? limitClause? ';'?
    ;

query_expression
    : (query_specification | '(' query_expression ')')
;

query_specification
    : SELECT
      select_list
      (FROM table_sources)?
      (WHERE where=search_condition)?
      (GROUP BY (ALL)? group_by_item (',' group_by_item)*)?
;

select_list
    : select_list_elem (',' select_list_elem)*
;

select_list_elem
    : asterisk
    | column_elem
    | expression_elem
;

asterisk
    : '*'
    | table_name '.' asterisk
;

table_name
    : table=id
;

id
    : simple_id
    | DOUBLE_QUOTE_ID
    | SQUARE_BRACKET_ID
    ;

simple_id
    : ID
    ;

table_sources
    : table_source (',' table_source)*
    ;

table_source
    : table_source_item_joined
    | '(' table_source_item_joined ')'
    ;

table_source_item_joined
    : table_source_item join_part*
    ;


table_source_item
    : full_table_name as_table_alias?
    ;

full_table_name
    : table=id
    ;

join_part
    : (INNER? |
       join_type=(LEFT | RIGHT | FULL) OUTER?)
       JOIN table_source ON search_condition
    ;

search_condition
    : search_condition_and (OR search_condition_and)*
    ;

search_condition_and
    : search_condition_not (AND search_condition_not)*
    ;

search_condition_not
    : NOT? predicate
    ;

predicate
    : expression comparison_operator expression #Comparison
    | expression NOT? BETWEEN expression AND expression #Between
    | expression NOT? IN '(' (subquery | expression_list) ')' #In
    | expression NOT? LIKE expression #Like
    | '(' search_condition ')' #Brackets
    ;

primitive_expression
    : DEFAULT | NULL | LOCAL_ID | constant
    ;

constant
    : STRING
    | BINARY
    | sign? DECIMAL
    | sign? (REAL | FLOAT)
    | sign? dollar='$' (DECIMAL | FLOAT)
    ;

sign
    : '+'
    | '-'
    ;

expression
    : full_column_name
    | function_call
    | bracket_expression
    ;

full_column_name
    : column_name=id
;

comparison_operator
    : '=' | '>' | '<' | '<' '=' | '>' '=' | '<' '>' | '!' '='
    ;

column_elem
    : (table_name '.')? column_name=id as_column_alias?
    ;

expression_elem
    : expression as_column_alias?
    ;

as_column_alias
    : AS? column_alias
    ;

as_table_alias
    : AS? table_alias
    ;

column_alias
    : id
    | STRING
    ;

table_alias
    : id
    | STRING
    ;

expression_list
    : expression (',' expression)*
    ;

bracket_expression
    : '(' expression ')' | '(' subquery ')'
    ;

subquery
    : select_statement
    ;

group_by_item
    : full_column_name
    ;

order_by_clause
    : ORDER BY order_by_expression (',' order_by_expression)*
    ;

order_by_expression
    : full_column_name (ASC | DESC)?
    ;

limitClause
    : LIMIT
    (
      (offset=limitClauseAtom ',')? limit=limitClauseAtom
      | limit=limitClauseAtom OFFSET offset=limitClauseAtom
    )
    ;

limitClauseAtom
	: decimalLiteral
    ;

decimalLiteral
    : DECIMAL
    ;

function_call
    : ID '(' full_column_name ( ',' full_column_name )* ')';