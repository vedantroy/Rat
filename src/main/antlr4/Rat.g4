grammar Rat;

program: declaration+;


declaration: varDeclaration
           | statement
           ;

varDeclaration: 'var' IDENTIFIER ('=' expression)?';';
statement: exprStmt
         | assertStmt
         | printStmt
         | block
         ;

exprStmt: expression';';
assertStmt: 'assert' expression';';
printStmt: 'print' expression';';
block: '{' declaration* '}';

expression: assignment;
assignment: IDENTIFIER '=' assignment
          | logical_or
          ;
logical_or: logical_and ('or' logical_and)*;
logical_and: equality ('and' equality)*;
equality: comparison (op=('==' | '!=') comparison)*;
comparison: addition  (op=('>' | '>=' | '<' | '<=') addition)* ;
addition: multiplication (op=('-' | '+') multiplication)* ;
multiplication: unary (op=( '/' | '*' ) unary )* ;
unary: op=( '!' | '-' ) unary
     | primary
     ;

/*
expression: IDENTIFIER '=' expression
          | expression op=('==' | '!=') expression
          | expression op=('>' | '>=' | '<' | '<=') expression
          | expression op=('-' | '+') expression
          | expression op=( '/' | '*' ) expression
          | op=( '!' | '-' ) expression
          | primary
          ;
*/

primary: intLiteral
       | booleanLiteral
       | stringLiteral
       | identifier
       | group
       ;

intLiteral: NUMBER;
booleanLiteral: value=('True' | 'False');
stringLiteral: STRING;
identifier: IDENTIFIER;
group: '(' expression ')';

TRUE: 'True';
FALSE: 'False';
NUMBER:   [0-9]+ ;
STRING: '"' ~('\n'|'"')* '"' ;
IDENTIFIER :   [a-zA-Z]+ ;

WS: ([ \t\n]+ | '\r\n') -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;