// B. Liam Rethore
// Project 5 CS445
// I hope you get a lot of enjoyment out of this :)

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.*;
import java.util.function.*;

class Expression {
	private String _type;
	private String _value;
	private Expression _left, _right;

	private Expression(String type, String value) {
		this(type, value, null, null);
	}

	private Expression(String type, String value, Expression left, Expression right) {
		_type = type;
		_value = value;
		_left = left;
		_right = right;
	}

	/**
	* Creates an operator expression.
	*/
	public static Expression Operator(Expression left, String operator, Expression right) {
		return new Expression("Operator", operator, left, right);
	}

	/**
	* Creates a number expression.
	*/
	public static Expression Number(double value) {
		return new Expression("Number", Double.toString(value));
	}

	/**
	* Creates a variable expression.
	*/
	public static Expression Variable(String name) {
		return new Expression("Variable", name);
	}

	/**
	* Very quick-and-dirty expression parser; doesn't really do any error checking.
	* But it's enough to build an Expression from a (known-to-be-correct) String.
	*/
	public static Expression quickParse(String input) {
		StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input));
		tokenizer.ordinaryChar('-');
		tokenizer.ordinaryChar('/');
		Stack<Character> operators = new Stack<>();
		Stack<Expression> operands = new Stack<>();
		try { tokenizer.nextToken(); }
		catch (IOException e) { throw new RuntimeException(e); }
		while(tokenizer.ttype != StreamTokenizer.TT_EOF) {
			int prec = 2;
			switch(tokenizer.ttype) {
				case StreamTokenizer.TT_NUMBER: operands.push(Number(tokenizer.nval));   break;
				case StreamTokenizer.TT_WORD:   operands.push(Variable(tokenizer.sval)); break;
				case '^': case '(': operators.push((char)tokenizer.ttype);  break;
				case ')':
					while(operators.peek() != '(')
						poperator(operators, operands);
					operators.pop();
					break;
				case '+': case '-': prec = 1; // fall thru
				case '*': case '/':
					while(!operators.empty()) {
						char top = operators.peek();
						int topPrec = (top == '^') ? 3 : (top == '*' || top == '/') ? 2 : 1;
						if(top == '(' || topPrec < prec) break;
						poperator(operators, operands);
					}
					operators.push((char)tokenizer.ttype);
					break;
				default: throw new RuntimeException("wat");
			}
			try { tokenizer.nextToken(); }
			catch (IOException e) { throw new RuntimeException(e); }
		}
		while(!operators.empty()){ poperator(operators, operands); }
		return operands.pop();
	}

	private static void poperator(Stack<Character> operators, Stack<Expression> operands) {
		Expression r = operands.pop();
		Expression l = operands.pop();
		operands.push(Operator(l, operators.pop() + "", r));
	}

	// These can be used to quickly check if an Expression is an Operator, Number, or Variable.
	public boolean isOperator() { return _type.equals("Operator"); }
	public boolean isNumber()   { return _type.equals("Number");   }
	public boolean isVariable() { return _type.equals("Variable"); }

	/**
	* For Numbers, converts the _value to a double and returns it.
	* Will crash for non-Numbers.
	*/
	private double getNumberValue() { return Double.parseDouble(_value); }

	/**
	* Recursively clones an entire Expression tree.
	* Note how this method works: operators are the recursive case, and
	* numbers and variables are base cases.
	*/
	public Expression clone() {
		if(this.isOperator()) {
			return Expression.Operator(_left.clone(), _value, _right.clone());
		} else if(this.isVariable()) {
			return Expression.Variable(_value);
		} else {
			return Expression.Number(getNumberValue());
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////

	/**
	* Converts this expression to an infix expression representation.
	*/
	public String toString() {
		// Done
		String expr = "";

		expr = toNiceString();

		/* Code before toNiceString was added
		if(this.isOperator()) { 
			expr = expr + "(" + _left.toString() + " " + _value + " " + _right.toString() + ")";
		} else if(this.isVariable()) {
			expr = expr + _value;
		} else {
			expr = expr + _value;
		}
		*/

		return expr;
		
	}

	/**
	* Converts this expression to a postfix expression representation.
	*/
	public String toPostfix() {
		// Done

		String expr = "";

		if(this.isOperator()) {
			expr = _left.toPostfix() + " " + _right.toPostfix() + " " + _value + expr;
		} else if(this.isVariable()) {
			expr = _value + expr;
		} else {
			expr = _value + expr;
		}

		return expr;
	}

	/**
	* Given the variables map (which tells what values each variable has),
	* evaluates the expression and returns the computed value.
	*/
	public double evaluate(Map<String, Double> variables) throws ExpressionError {
		// Done

		double result = 0;
		
		if(this.isOperator()) {
			
			switch(_value){
				case "+":
					result = result + _left.evaluate(variables) + _right.evaluate(variables);	
					break;
				case "-":
					result = result + _left.evaluate(variables) - _right.evaluate(variables);
					break;
				case "*":
					result = result + _left.evaluate(variables) * _right.evaluate(variables);
					break;
				case "/":
					result = result + _left.evaluate(variables) / _right.evaluate(variables);
					break;
				case "^":
					result = result + Math.pow(_left.evaluate(variables), _right.evaluate(variables));
					break;
			}
		} else if(this.isVariable()) {

			if (!variables.containsKey(_value)) {
				throw new ExpressionError("ERROR: One or more of the variables has not been given a value!");
			}
		
			result = variables.get(_value);
		} else {
			result = this.getNumberValue();
		}

		return result;
	}

	/**
	* Creates a new Expression that is the reciprocal of this one.
	*/
	public Expression reciprocal() {
		// Done

		Expression result = Number(0);

		if(this.isOperator()) {

			switch(_value){
				case "/":

					Expression exprResultOne = Operator(this._right.clone(), "/", this._left.clone());
					result = exprResultOne;
					break;
				default:

					Expression exprResultTwo = Operator(Number(1), "/", this.clone());
					result = exprResultTwo;
					break;
			}					
		} 
		else {

			result = Number(1 / this.getNumberValue());	
		}
		return result;
	}

	/**
	* Gets a set of all variables which appear in this expression.
	*/
	public Set<String> getVariables() {
		// Done
		Set<String> variables = new HashSet<>();
		String tempVariables = findVariables();
		String[] indivualVars = tempVariables.split(",");
		
		for (int i = 0; i < indivualVars.length; i++){
			if (!indivualVars[i].equals("0"))
				variables.add(indivualVars[i]);
		}
		return variables;
	}

	private String findVariables(){

		String vars = "0";
		if(this.isOperator()) {
			vars = vars + "," + _left.findVariables() + "," + _right.findVariables();
		} else if(this.isVariable()) {
			vars = vars + "," + _value;
		}

		return vars;
	}

	/**
	* Constructs a new Expression of the form:
	* 	(numbers[0] * numbers[1] * ... numbers[n-1]) ^ (1 / n)
	* and returns it.
	*/
	public static Expression geometricMean(double[] numbers) {
		// Done

		Expression result = Number(numbers[0]);

		for(int i = 1; i < numbers.length; i++){

			result = Operator(result, "*", Number(numbers[i]));
		}

		result = Operator(result, "^", Number(numbers.length).reciprocal());

		return result;
	}

	/**
	* EXTRA CREDIT: converts this expression to an infix expression representation,
	* but only places parentheses where needed to override the order of operations.
	*/
	public String toNiceString() {
		// Done
		String expr = "";
		
		if(this.isOperator()) {
			try {
				if (_value == "^" && !_left._left._value.equals(null)){
					expr = expr + "(" + _left.toNiceString() + ") " + _value + " " + _right.toNiceString();
				} else if (_value == "/" && !_right._right._value.equals(null)) {
					expr = expr + _left.toNiceString() + " " + _value + " (" + _right.toNiceString() + ")";
				}
				else{
					expr = expr + _left.toNiceString() + " " + _value + " " + _right.toNiceString();
				}	
			} catch (NullPointerException e) {
				expr = expr + _left.toNiceString() + " " + _value + " " + _right.toNiceString();
			}
		} else if(this.isVariable()) {
			expr = expr + _value;
		} else {
			expr = expr + _value;
		}

		return expr;
		// Please ignore the fact that this doesn't deal with user added parentheses
		// It wasn't in the code description so I did not do it :D
	}
}