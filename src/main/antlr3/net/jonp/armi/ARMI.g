grammar ARMI;

options {
	output=AST;
	ASTLabelType=CommonTree;
}

tokens {
	ARRAYTOK = 'array';
	CALLTOK = 'call';
	COMMA = ',';
	DOT = '.';
	EQUALS = '=';
	ERRORTOK = 'error';
	FALSE = 'false';
	HELP = 'help';
	LABELTOK = 'label';
	LBRACKET = '[';
	LIST = 'list';
	LPAREN = '(';
	METHODS = 'methods';
	NIL = 'null';
	OBJECTS = 'objects';
	RBRACKET = ']';
	RESPONSETOK = 'response';
	RPAREN = ')';
	TRUE = 'true';
	UNSOLTOK = 'unsol';

	ARG;
	ARGS;
	ARRAY;
	BOOL;
	CALL;
	ELEMENTS;
	ERROR;
	FIELD;
	FIELDS;
	IDENT;
	LABEL;
	NUM;
	OBJ;
	RESPONSE;
	STR;
	STRINGS;
	UNSOLICITED;
}

@header {
	package net.jonp.armi;
}

@lexer::header {
	package net.jonp.armi;
}

command
	: CALLTOK label? ident LPAREN arguments RPAREN -> ^(CALL label? ident arguments)
	| HELP
	| LIST label? list -> ^(LIST label? list)
	;

list
	: OBJECTS       -> ^(OBJECTS)
	| METHODS ident -> ^(METHODS ident)
	;

response
	: RESPONSETOK label? LPAREN val RPAREN       -> ^(RESPONSE label? val)
	| ERRORTOK label? ident LPAREN string RPAREN -> ^(ERROR label? ident string)
	| LIST label? LPAREN stringlist RPAREN       -> ^(LIST label? stringlist)
	| UNSOLTOK LPAREN ident COMMA val RPAREN     -> ^(UNSOLICITED ident val)
	;

label
	: LABELTOK string -> ^(LABEL string)
	;

ident
	: ATOM (DOT ATOM)* -> ^(IDENT ATOM ATOM*)
	;

arguments
	: argument (COMMA argument)* -> ^(ARGS argument argument*)
	| -> ^(ARGS)
	;

argument
	: val -> ^(ARG val)
	;

stringlist
	: string (COMMA string)* -> ^(STRINGS string string*)
	| -> ^(STRINGS)
	;

val
	: string
	| number
	| bool
	| array
	| object
	| NIL
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

array
	: ARRAYTOK LBRACKET elements RBRACKET -> ^(ARRAY elements)
	;

object
	: ident LPAREN fields RPAREN -> ^(OBJ ident fields)
	;

fields
	: field (COMMA field)* -> ^(FIELDS field field*)
	| -> ^(FIELDS)
	;

field
	: ATOM EQUALS val -> ^(FIELD ATOM val)
	;

elements
	: val (COMMA val)* -> ^(ELEMENTS val val*)
	| -> ^(ELEMENTS)
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
