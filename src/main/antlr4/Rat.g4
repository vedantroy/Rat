grammar Rat;

program: declaration+;


declaration: varDeclaration
           | statement
           ;

varDeclaration: 'var' IDENTIFIER ('=' expression)?';';
statement: exprStmt
         | ifStmt
         | whileStmt
         | assertStmt
         | printStmt
         | block
         ;

exprStmt: expression';';
ifStmt: 'if' '(' expression ')' statement ('else' statement)?;
whileStmt: 'while' '(' expression ')' statement;
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
expression: primary
          | op=( '!' | '-' ) expression
          | expression op=( '/' | '*' ) expression
          | expression op=('-' | '+') expression
          | expression op=('>' | '>=' | '<' | '<=') expression
          | expression op=('==' | '!=') expression
          | expression op='and' expression
          | expression op='or' expression
          | IDENTIFIER '=' expression
          ;
*/

primary: nilLiteral
       | intLiteral
       | booleanLiteral
       | stringLiteral
       | identifier
       | group
       ;

nilLiteral: NIL;
intLiteral: NUMBER;
booleanLiteral: value=('True' | 'False');
stringLiteral: STRING;
identifier: IDENTIFIER;
group: '(' expression ')';

NIL: 'Nil';
TRUE: 'True';
FALSE: 'False';
NUMBER:   [0-9]+ ;
STRING: '"' ~('\n'|'"')* '"' ;
IDENTIFIER :   [a-zA-Z]+ ;

WS: ([ \t\n]+ | '\r\n') -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;