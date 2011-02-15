package org.xmlcml.cml.converters.format;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;

public class Operator implements Comparable<Operator> {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(Operator.class);

	public enum Associativity {
		LEFT,
		RIGHT
		;
	}
	
	public static String AND      = CMLConstants.S_AMP+CMLConstants.S_AMP;
	public static String DIVIDE   = CMLConstants.S_SLASH;
	public static String EQ       = CMLConstants.S_EQUALS+CMLConstants.S_EQUALS;
	public static String GE       = CMLConstants.S_RANGLE+CMLConstants.S_EQUALS;
	public static String GT       = CMLConstants.S_RANGLE;
	public static String LE       = CMLConstants.S_LANGLE+CMLConstants.S_EQUALS;
	public static String LT       = CMLConstants.S_LANGLE;
	public static String MINUS    = CMLConstants.S_MINUS;
	public static String MULTIPLY = CMLConstants.S_STAR;
	public static String NE       = CMLConstants.S_SHRIEK+CMLConstants.S_EQUALS;
	public static String OR       = CMLConstants.S_PIPE+CMLConstants.S_PIPE;
	public static String PLUS     = CMLConstants.S_PLUS;
	
	public static String ESC      = CMLConstants.S_BACKSLASH;
	public static String PIPE     = CMLConstants.S_PIPE;
	public static String ESC_OR   = ESC+PIPE+ESC+PIPE;

	public static String LBRAK    = CMLConstants.S_LBRAK;
	public static String RBRAK    = CMLConstants.S_RBRAK;

	public static String COMMA    = CMLConstants.S_COMMA;

	public static final String BRACKET_REGEX = ESC+LBRAK+PIPE+ESC+RBRAK;
	
	public static final String REGEX = 
		AND+PIPE+
		DIVIDE+PIPE+
		EQ+PIPE+
		GE+PIPE+
		GT+PIPE+
		LE+PIPE+
		LT+PIPE+
		ESC+MINUS+PIPE+
		ESC+MULTIPLY+PIPE+
		NE+PIPE+
		ESC_OR+PIPE+
		ESC+PLUS;
	
/**
Precedence 	Operator 	Description 	Associativity
1 	:: 	Scope resolution (C++ only) 	Left-to-right
2 	++ -- 	Suffix/postfix increment and decrement
() 	Function call
[] 	Array subscripting
. 	Element selection by reference
-> 	Element selection through pointer
typeid() 	Run-time type information (C++ only) (see typeid)
const_cast 	Type cast (C++ only) (see const_cast)
dynamic_cast 	Type cast (C++ only) (see dynamic_cast)
reinterpret_cast 	Type cast (C++ only) (see reinterpret_cast)
static_cast 	Type cast (C++ only) (see static_cast)
3 	++ -- 	Prefix increment and decrement 	Right-to-left
+ - 	Unary plus and minus
! ~ 	Logical NOT and bitwise NOT
(type) 	Type cast
* 	Indirection (dereference)
& 	Address-of
sizeof 	Size-of
new, new[] 	Dynamic memory allocation (C++ only)
delete, delete[] 	Dynamic memory deallocation (C++ only)
4 	.* ->* 	Pointer to member (C++ only) 	Left-to-right
5 	* / % 	Multiplication, division, and modulus (remainder)
6 	+ - 	Addition and subtraction
7 	<< >> 	Bitwise left shift and right shift
8 	< <= 	For relational operators < and <= respectively
> >= 	For relational operators > and >= respectively
9 	== != 	For relational = and != respectively
10 	& 	Bitwise AND
11 	^ 	Bitwise XOR (exclusive or)
12 	| 	Bitwise OR (inclusive or)
13 	&& 	Logical AND
14 	|| 	Logical OR
15 	c ? t : f 	Ternary conditional (see ?:) 	Right-to-Left
16 	= 	Direct assignment (provided by default for C++ classes)
+= -= 	Assignment by sum and difference
*= /= %= 	Assignment by product, quotient, and remainder
<<= >>= 	Assignment by bitwise left shift and right shift
&= ^= |= 	Assignment by bitwise AND, XOR, and OR
17 	throw 	Throw operator (exceptions throwing, C++ only)
18 	, 	Comma 	Left-to-right 
*/

	/*
	Precedence 	Operator 	Description 	Associativity
	2    () 	Function call                    LEFT
	3   + - 	Unary plus and minus             RIGHT
	    ! ~ 	Logical NOT and bitwise NOT RIGHT
	5 	* / % 	Multiplication, division, and modulus (remainder) LEFT
	6 	+ - 	Addition and subtraction LEFT
	8 	< <= 	For relational operators < and <= respectively LEFT
	    > >= 	For relational operators > and >= respectively
	9 	== != 	For relational = and != respectively LEFT
	13 	&& 	Logical AND LEFT
	14 	|| 	Logical OR LEFT
	16 	= 	Direct assignment (provided by default for C++ classes) RIGHT
	18 	, 	Comma 	LEFT
	*/
	public static Operator MINUS_OP = new Operator(MINUS, 3, Associativity.LEFT);
	public static Operator PLUS_OP = new Operator(PLUS, 3, Associativity.LEFT);
	
	private static Map<String, Operator> operatorByName = new HashMap<String, Operator>();
	static {
		operatorByName.put(MINUS_OP.getName(), MINUS_OP);
		operatorByName.put(PLUS_OP.getName(), PLUS_OP);
	}
	public static Operator getOperator(String name) {
		return operatorByName.get(name);
	}
	
	private final static Pattern OPERATOR_PATTERN = Pattern.compile(REGEX);
	
	private String name;
	private int priority;
	private Associativity associativity;
	
	private Operator(String s, int p, Associativity assoc) {
		name = s;
		priority = p;
		associativity = assoc;
	}
	
	public String getName() {
		return name;
	}

	public static boolean isOperator(String s) {
		return OPERATOR_PATTERN.matcher(s).matches();
	}

	public Associativity getAssociativity() {
		return associativity;
	}

	public int compareTo(Operator o2) {
		return this.priority - o2.priority;
	}

	public boolean isRightAssociative() {
		return false;
	}
}
