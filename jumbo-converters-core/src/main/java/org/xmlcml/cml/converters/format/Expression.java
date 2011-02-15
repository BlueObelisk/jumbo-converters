package org.xmlcml.cml.converters.format;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.converters.format.Operator.Associativity;

public class Expression {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(Expression.class);
	
	private static final String EXPRESSION = "expression";

	private Element ast;

	private List<String> tokens;

	private Stack<Object> stack;
	private Queue<Object> queue;
	
	public Expression() {
		
	}

	/*
	 * Shunting yard (Wikipedia)
	 * 
    * While there are tokens to be read:

        * Read a token.
        * If the token is a number, then add it to the output queue.
        * If the token is a function token, then push it onto the stack.
        * If the token is a function argument separator (e.g., a comma):

            * Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue. If no left parentheses are encountered, either the separator was misplaced or parentheses were mismatched.

        * If the token is an operator, o1, then:

            * while there is an operator token, o2, at the top of the stack, and

                    either o1 is left-associative and its precedence is less than or equal to that of o2,
                    or o1 is right-associative and its precedence is less than that of o2,

                pop o2 off the stack, onto the output queue;

            * push o1 onto the stack.

        * If the token is a left parenthesis, then push it onto the stack.
        * If the token is a right parenthesis:

            * Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
            * Pop the left parenthesis from the stack, but not onto the output queue.
            * If the token at the top of the stack is a function token, pop it onto the output queue.
            * If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.

    * When there are no more tokens to read:

        * While there are still operator tokens in the stack:

            * If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
            * Pop the operator onto the output queue.

    * Exit.
	 */
	public void readInfix(String s) {
		tokens = parseTokens(s);
		shuntTokens();
		processStackAndQueue();
	}
	
	public List<String> parseTokens(String s) {
		if (s == null) {
			throw new RuntimeException("Null expression");
		}
		List<String> list = new ArrayList<String>();
		s = s.replaceAll(CMLConstants.S_WHITEREGEX, CMLConstants.S_EMPTY);
		Pattern pattern = Pattern.compile("("+Operator.REGEX+
				"|"+Operator.BRACKET_REGEX+
				"|"+Symbol.SYMBOL_REGEX+
				"|"+Symbol.SYMBOL_REGEX+
				"|"+Operator.COMMA+
				")");
		Matcher matcher = pattern.matcher(s);
		StringBuilder sb =new StringBuilder(s);
		int i = 0;
		while (i < sb.length() && matcher.find(i) ) {
			if (matcher.start() != i) {
				throw new RuntimeException("unknown token at: "+sb.substring(i));
			}
			i = matcher.end();
			String match = matcher.group(1);
			list.add(match);
		}
		return list;
	}

	private void shuntTokens() {
		stack = new Stack<Object>();
		queue = new LinkedList<Object>();
		for (int i = 0; i < tokens.size(); i++) {
			String token = tokens.get(i);
			
//	        * If the token is a left parenthesis, then push it onto the stack.
			if (Operator.LBRAK.equals(token)) {
				stack.push(token);
				continue;
			}
			
//	        * If the token is a right parenthesis:
			if (Operator.RBRAK.equals(token)) {
				processRightBracket(token);
				continue;
			}
			
//	        * If the token is a function token, then push it onto the stack.
			Function function = Function.createFunction(token);
			if (function != null) {
				stack.push(token);
				continue;
			}
			
//	        * If the token is a number (or symbol), then add it to the output queue.
			Variable v = Variable.createVariable(token);
			if (v != null) {
				queue.add(v);
				continue;
			}
			
//	        * If the token is a function argument separator (e.g., a comma):
			if (Operator.COMMA.equals(token)) {
				processComma(token);
				continue;
			}
			
//	        * If the token is an operator, o1, then:
			Operator operator = Operator.getOperator(token);
			if (operator != null) {
				processOperator(operator);
				continue;
			}
		}
	}

	private void processOperator(Operator o1) {
//        * while there is an operator token, o2, at the top of the stack, and
//        either o1 is left-associative and its precedence is less than or equal to that of o2,
//        or o1 is right-associative and its precedence is less than that of o2,
//	    pop o2 off the stack, onto the output queue;
		while (true) {
			Object o2o = stack.peek();
			if (o2o == null || !(o2o instanceof Operator)) {
				break;
			}
			Operator o2 = (Operator) o2o;
			// simplify 
			if ((o1.getAssociativity().equals(Associativity.LEFT) && (o1.compareTo(o2) <= 0)) ||
					(o1.getAssociativity().equals(Associativity.RIGHT) && (o1.compareTo(o2) < 0))
				) {
				stack.pop();
				queue.add(o2);
			}
		}
		
//* push o1 onto the stack.
		stack.push(o1);
	}

	private void processRightBracket(String token) {
		//
//        * Until the token at the top of the stack is a left parenthesis, pop operators off the stack onto the output queue.
//        * Pop the left parenthesis from the stack, but not onto the output queue.
//        * If the token at the top of the stack is a function token, pop it onto the output queue.
//        * If the stack runs out without finding a left parenthesis, then there are mismatched parentheses.
	}

	private void processComma(String token) {
//        * Until the token at the top of the stack is a left parenthesis, pop operators 
//        * off the stack onto the output queue. If no left parentheses are encountered, 
//        * either the separator was misplaced or parentheses were mismatched.
		throw new RuntimeException("Comma not yet supported");
	}

	private void processStackAndQueue() {
//	    * When there are no more tokens to read:
//
//	        * While there are still operator tokens in the stack:
//
//	            * If the operator token on the top of the stack is a parenthesis, then there are mismatched parentheses.
//	            * Pop the operator onto the output queue.
//	            */
		
	}

	public Element getAST() {
		ast = new Element(EXPRESSION);
		return ast;
	}
}
