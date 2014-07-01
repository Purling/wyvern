package wyvern.tools.types.extensions;

import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.types.JavaClassType;
import wyvern.tools.types.*;
import wyvern.tools.util.TreeWriter;

import java.util.*;

import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY;
import static wyvern.tools.errors.ErrorMessage.OPERATOR_DOES_NOT_APPLY2;
import static wyvern.tools.errors.ToolError.reportError;

public class Int extends AbstractTypeImpl implements OperatableType {
	private Int() { }
	private static Int instance = new Int();
	public static Int getInstance() { return instance; }
	
	private static final Set<String> legalOperators = new HashSet<String>(Arrays.asList(new String[] {
			"+",
			"-",
			"*",
			"/",
			">",
			"<",
			">=",
			"<=",
			"==",
			"!=",
	}));
	
	@Override
	public Type checkOperator(Invocation opExp, Environment env) {
		Type type2 = opExp.getArgument().typecheck(env, Optional.empty());
		String operatorName = opExp.getOperationName();
		
		if (!(legalOperators.contains(operatorName)))
			reportError(OPERATOR_DOES_NOT_APPLY, opExp, operatorName, this.toString());
		
		if (!((type2 instanceof Int) || ((operatorName.equals("+")) && (type2 instanceof Str))))
			reportError(OPERATOR_DOES_NOT_APPLY2, opExp, operatorName, this.toString(), type2.toString());
		
		if (isRelationalOperator(operatorName))
			return Bool.getInstance(); //relational operations
		else if ((operatorName.equals("+")) && (type2 instanceof Str)) {
			return Str.getInstance(); //string concatenation
		} else {
			return this; //arithmetic operations
		}
	}
	
	@Override
	public void writeArgsToTree(TreeWriter writer) {
		// nothing to write		
	}
	
	@Override
	public String toString() {
		return "Int";
	}

	private boolean isRelationalOperator(String operatorName) {
		return operatorName.equals(">") || operatorName.equals("<") || operatorName.equals("!=")
			|| operatorName.equals(">=") || operatorName.equals("<=") || operatorName.equals("==")	
			|| operatorName.equals("!=");
	}

	@Override
	public boolean subtype(Type other, HashSet<SubtypeRelation> subtypes) {
		if (other instanceof JavaClassType) {
			if (Util.javaToWyvType(Integer.class).subtype(other, subtypes))
				return true;
			return ((JavaClassType) other).getInnerClass().equals(Object.class);
		}
		return super.subtype(other, subtypes);
	}


	@Override
	public Map<String, Type> getChildren() {
		return new HashMap<>();
	}

	@Override
	public Type cloneWithChildren(Map<String, Type> newChildren) {
		return this;
	}
}