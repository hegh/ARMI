grammar ARMI;

options {
	output=AST;
	ASTLabelType=CommonTree;
}

tokens {
	ARRAYTOK = 'array';
	BYTETOK = 'Y';
	CALLTOK = 'call';
	COLLECTIONTOK = 'collection';
	COMMA = ',';
	DOT = '.';
	EQUALS = '=';
	ERRORTOK = 'error';
	FALSE = 'false';
	FLOATTOK = 'F';
	HELP = 'help';
	LABELTOK = 'label';
	LBRACKET = '[';
	LONGTOK = 'L';
	LPAREN = '(';
	MAPTOK = 'map';
	NIL = 'null';
	RBRACKET = ']';
	RESPONSETOK = 'response';
	RPAREN = ')';
	SHORTTOK = 'T';
	TRUE = 'true';
	UNSOLTOK = 'unsol';

	ARG;
	ARGS;
	ARRAY;
	BOOL;
	BYTE;
	CALL;
	COLLECTION;
	ELEMENTS;
	ERROR;
	FIELD;
	FIELDS;
	FLOAT;
	IDENT;
	LABEL;
	LONG;
	MAP;
	MAPVAL;
	MAPVALS;
	NUM;
	NUMDEFAULT;
	OBJ;
	RESPONSE;
	SHORT;
	STR;
	UNSOLICITED;
}

@header {
	package net.jonp.armi;
}

@lexer::header {
	package net.jonp.armi;
}

command
	: CALLTOK label? ident LPAREN arguments RPAREN EOF -> ^(CALL label? ident arguments)
	| HELP EOF                                         -> ^(HELP)
	;

response
	: RESPONSETOK label? LPAREN val RPAREN EOF       -> ^(RESPONSE label? val)
	| ERRORTOK label? ident LPAREN string RPAREN EOF -> ^(ERROR label? ident string)
	| UNSOLTOK LPAREN ident COMMA val RPAREN EOF     -> ^(UNSOLICITED ident val)
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

val
	: string
	| number
	| bool
	| array
	| collection
	| map
	| object
	| NIL
	;

string
	: STRING -> ^(STR STRING)
	;

number
	: INTEGER (DOT INTEGER)? numtype -> ^(NUM numtype INTEGER (DOT INTEGER)?)
	;

numtype
	: BYTETOK  -> ^(BYTE)
	| FLOATTOK -> ^(FLOAT)
	| LONGTOK  -> ^(LONG)
	| SHORTTOK -> ^(SHORT)
	|          -> ^(NUMDEFAULT)
	;

bool
	: TRUE -> ^(BOOL TRUE)
	| FALSE -> ^(BOOL FALSE)
	;

array
	: ARRAYTOK LPAREN ident RPAREN LBRACKET elements RBRACKET -> ^(ARRAY ident elements)
	;

collection
	: COLLECTIONTOK LPAREN ident RPAREN LBRACKET elements RBRACKET -> ^(COLLECTION ident elements)
	;

map
	: MAPTOK LPAREN ident RPAREN LBRACKET mapvals RBRACKET -> ^(MAP ident mapvals)
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

mapvals
	: mapval (COMMA mapval)* -> ^(MAPVALS mapval mapval*)
	| -> ^(MAPVALS)
	;

mapval
	: val EQUALS val -> ^(MAPVAL val val)
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
	: '-'? '0'..'9'+
	;

WS
	: ('\t' | ' ' | '\r' | '\n')+ { $channel = HIDDEN; }
	;

COMMENT
	: '#' ~'\n'* '\n' { $channel = HIDDEN; }
	;
