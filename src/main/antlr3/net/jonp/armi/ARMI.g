grammar ARMI;

options {
	output=AST;
	ASTLabelType=CommonTree;
}

tokens {
	CALLTOK = 'call';
	COMMA = ',';
	DOT = '.';
	EQUALS = '=';
	ERRORTOK = 'error';
	FALSE = 'false';
	LABELTOK = 'label';
	LPAREN = '(';
	RESPONSETOK = 'response';
	RPAREN = ')';
	TRUE = 'true';

	ARG;
	ARGS;
	BOOL;
	CALL;
	ERROR;
	FIELD;
	FIELDS;
	IDENT;
	LABEL;
	NUM;
	OBJ;
	RESPONSE;
	STR;
}

@header {
	package net.jonp.armi;
}

@lexer::header {
	package net.jonp.armi;
}

command
	: CALLTOK label? ident LPAREN arguments RPAREN -> ^(CALL label? ident arguments)
	;

response
	: RESPONSETOK label? LPAREN val RPAREN       -> ^(RESPONSE label? val)
	| ERRORTOK label? ident LPAREN string RPAREN -> ^(ERROR label? ident string)
	;

label
	: LABELTOK string -> ^(LABEL string)
	;

ident
	: ATOM (DOT ATOM)* -> ^(IDENT ATOM ATOM*)
	;

arguments
	: argument (COMMA argument)* -> ^(ARGS argument argument*)
	;

argument
	: val -> ^(ARG val)
	;

val
	: string
	| number
	| bool
	| object
	;

string
	: STRING -> ^(STR STRING)
	;

number
	: INTEGER (DOT INTEGER)? -> ^(NUM INTEGER (DOT INTEGER)?)
	;

bool
	: TRUE -> ^(BOOL TRUE)
	| FALSE -> ^(BOOL FALSE)
	;

object
	: ident LPAREN fields RPAREN -> ^(OBJ ident fields)
	;

fields
	: field (COMMA field)* -> ^(FIELDS field field*)
	;

field
	: ATOM EQUALS val -> ^(FIELD ATOM val)
	;

STRING
	: '\"' (('\\' .) | ~('\\' | '\"'))* '\"' {
		char[] c = getText().substring(1, getText().length() - 1).toCharArray();
		StringBuilder txt = new StringBuilder("");
		boolean esc = false;
		for (int i = 0; i < c.length; i++) {
			if (esc) {
				txt.append(c[i]);
				esc = false;
			}
			else if (c[i] == '\\') {
				esc = true;
			}
			else {
				txt.append(c[i]);
			}
		}
		setText(txt.toString());
	}
	;

ATOM
	: ('a'..'z' | 'A'..'Z' | '$' | '_') ('a'..'z' | 'A'..'Z' | '0'..'9' | '$' | '_')*
	;

INTEGER
	: '0'..'9'+
	;

WS
	: ('\t' | ' ' | '\r' | '\n')+ { $channel = HIDDEN; }
	;

COMMENT
	: '#' ~'\n'* '\n' { $channel = HIDDEN; }
	;
