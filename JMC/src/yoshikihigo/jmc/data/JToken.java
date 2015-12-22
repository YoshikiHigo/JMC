package yoshikihigo.jmc.data;

public enum JToken {

	BYTE("byte"), CHAR("char"), SHORT("short"), INT("int"), LONG("long"), FLOAT(
			"float"), DOUBLE("double"), BOOLEAN("boolean"), TRUE("true"), FALSE(
			"false"), VOID("void"), IF("if"), ELSE("else"), SWITCH("switch"), CASE(
			"case"), DEFAULT("default"), CONTINUE("continue"), BREAK("break"), RETURN(
			"return"), PACKAGE("package"), IMPORT("import"), CLASS("class"), INTERFACE(
			"interface"), EXTENDS("extends"), IMPLEMENTS("implements"), THIS(
			"this"), SUPER("super"), NEW("new"), NULL("null"), INSTANCEOF(
			"instanceof"), PUBLIC("public"), PROTECTED("protected"), PRIVATE(
			"private"), FINAL("final"), STATIC("static"), ABSTRACT("abstract"), NATIVE(
			"native"), SYNCHRONIZED("synchronized"), VOLATILE("volatile"), TRANSIENT(
			"transient"), FOR("for"), DO("do"), WHILE("while"), TRY("try"), CATCH(
			"catch"), FINALLY("finally"), THROW("throw"), THROWS("throws"), ASSERT(
			"assert"), ENUM("enum"), PLUS("+"), MINUS("-"), PRODUCT("*"), DIVIDE(
			"/"), MOD("%"), LESS("<"), GREAT(">"), LESSEQUAL("<="), GREATEQUAL(
			">="), EQUAL("=="), NOTEQUAL("!="), AND("&"), OR("|"), XOR("^"), NOT(
			"!"), ANDAND("&&"), OROR("||"), RIGHTSHIFT(">>"), RIGHTSHIFT2(">>>"), LEFTSHIFT(
			"<<"), INCREMENT("++"), DECREMENT("--"), PLUSASSIGNMENT("+="), MINUSASSIGNMENT(
			"-="), PRODUCTASSIGNMENT("*="), DIVIDEASSIGNMENT("/="), MODASSIGNMENT(
			"%="), ASSIGNMENT("="), ANDASSIGNMENT("&="), ORASSIGNMENT("|="), XORASSIGNMENT(
			"~="), RIGHTSHIFTASSIGNMENT(">>="), RIGHTSHIFT2ASSIGNMENT(">>>="), LEFTSHIFTASSIGNMENT(
			"<<="), HATENA("?"), COLON(":"), SEMICOLON(";"), COMMA(","), LEFTPAREN(
			"("), RIGHTPAREN(")"), LEFTBRACKET("{"), RIGHTBRACKET("}"), LEFTSQUAREBRACKET(
			"["), RIGHTSQUAREBRACKET("]"), LEFTANGLEBRACKET("<"), RIGHTANGLEBRACKET(
			">"), DOT("."), TYPE("$TYPE"), VARIABLE("$VAR"), METHOD("$METHOD"), NUMBER(
			"$NUM"), CHARACTER("$CHAR"), STRING("$STR");

	final public String value;

	private JToken(final String value) {
		this.value = value;
	}
}
