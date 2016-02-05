package jarDependencies;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import twg2.meta.fieldAccess.FieldGets;

import com.github.zafarkhaja.semver.Version;
import com.github.zafarkhaja.semver.expr.Expression;

/**
 * @author TeamworkGuy2
 * @since 2016-1-31
 */
public class VersionResolution {
	private static String baseName = "com.github.zafarkhaja.semver.expr.";


	public static enum Operator {
		AND() {
			@Override public boolean is(Expression expr) {
				return (baseName + "And").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildExpressions.add(OpAnd.getLeft(expr));
				dstChildExpressions.add(OpAnd.getRight(expr));
			}
		},

		EQUAL() {
			@Override public boolean is(Expression expr) {
				return (baseName + "Equal").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildVersions.add(OpEqual.getEqual(expr));
			}
		},

		GREATER() {
			@Override public boolean is(Expression expr) {
				return (baseName + "Greater").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildVersions.add(OpGreater.getGreater(expr));
			}
		},

		GREATER_OR_EQUAL() {
			@Override public boolean is(Expression expr) {
				return (baseName + "GreaterOrEqual").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildVersions.add(OpGreaterOrEqual.getGreaterOrEqual(expr));
			}
		},

		LESS() {
			@Override public boolean is(Expression expr) {
				return (baseName + "Less").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildVersions.add(OpLess.getLess(expr));
			}
		},

		LESS_OR_EQUAL() {
			@Override public boolean is(Expression expr) {
				return (baseName + "LessOrEqual").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildVersions.add(OpLessOrEqual.getLessOrEqual(expr));
			}
		},

		NOT() {
			@Override public boolean is(Expression expr) {
				return (baseName + "Not").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildExpressions.add(OpNot.getNot(expr));
			}
		},

		NOT_EQUAL() {
			@Override public boolean is(Expression expr) {
				return (baseName + "NotEqual").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildVersions.add(OpNotEqual.getNotEqual(expr));
			}
		},

		OR() {
			@Override public boolean is(Expression expr) {
				return (baseName + "Or").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildExpressions.add(OpOr.getLeft(expr));
				dstChildExpressions.add(OpOr.getRight(expr));
			}
		},

		COMPOSITE() {
			@Override public boolean is(Expression expr) {
				return (baseName + "CompositeExpression").equals(expr.getClass().getName());
			}

			@Override public void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions) {
				dstChildExpressions.add(OpComposite.getExpression(expr));
			}
		};


		private static final Operator[] ops = Operator.values();


		public abstract boolean is(Expression expr);

		public abstract void getChildExpressionsAndVersions(Expression expr, List<Expression> dstChildExpressions, List<Version> dstChildVersions);


		public static Operator getExpressionType(Expression expr) {
			for(Operator op : ops) {
				if(op.is(expr)) {
					return op;
				}
			}
			throw new IllegalArgumentException("unknown expression type " + expr);
		}

	}




	public interface OpAnd {

		public static Expression getLeft(Expression expr) {
			return (Expression) FieldGets.getSimpleField(expr.getClass(), "left").get(expr);
		}

		public static Expression getRight(Expression expr) {
			return (Expression) FieldGets.getSimpleField(expr.getClass(), "right").get(expr);
		}

	}


	public interface OpEqual {

		public static Version getEqual(Expression expr) {
			return (Version) FieldGets.getSimpleField(expr.getClass(), "parsedVersion").get(expr);
		}

	}


	public interface OpGreater {

		public static Version getGreater(Expression expr) {
			return (Version) FieldGets.getSimpleField(expr.getClass(), "parsedVersion").get(expr);
		}

	}


	public interface OpGreaterOrEqual {

		public static Version getGreaterOrEqual(Expression expr) {
			return (Version) FieldGets.getSimpleField(expr.getClass(), "parsedVersion").get(expr);
		}

	}


	public interface OpLess {

		public static Version getLess(Expression expr) {
			return (Version) FieldGets.getSimpleField(expr.getClass(), "parsedVersion").get(expr);
		}

	}


	public interface OpLessOrEqual {

		public static Version getLessOrEqual(Expression expr) {
			return (Version) FieldGets.getSimpleField(expr.getClass(), "parsedVersion").get(expr);
		}

	}


	public interface OpNot {

		public static Expression getNot(Expression expr) {
			return (Expression) FieldGets.getSimpleField(expr.getClass(), "expr").get(expr);
		}

	}


	public interface OpNotEqual {

		public static Version getNotEqual(Expression expr) {
			return (Version) FieldGets.getSimpleField(expr.getClass(), "parsedVersion").get(expr);
		}

	}


	public interface OpOr {

		public static Expression getLeft(Expression expr) {
			return (Expression) FieldGets.getSimpleField(expr.getClass(), "left").get(expr);
		}

		public static Expression getRight(Expression expr) {
			return (Expression) FieldGets.getSimpleField(expr.getClass(), "right").get(expr);
		}

	}


	public interface OpComposite {

		public static Expression getExpression(Expression expr) {
			return (Expression) FieldGets.getSimpleField(expr.getClass(), "exprTree").get(expr);
		}

	}




	public static final Version greatestCommonVersion(Expression a, Expression b, Iterable<Version> availableVersions) {
		for(Version ver : availableVersions) {
			if(a.interpret(ver) && b.interpret(ver)) {
				return ver;
			}
		}
		return null;
	}


	public static final String toString(Expression expr) {
		StringBuilder sb = new StringBuilder();
		toString(expr, sb);
		return sb.toString();
	}


	public static final void toString(Expression expr, Appendable dst) {
		try {
			_toString(expr, dst);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}


	/** Recursively convert an {@link Expression} tree or {@link Version} to a string
	 * @param expr the expression or version
	 * @param dst the destination to write the expression to
	 * @throws IOException if there is an exception writing to the {@code dst}
	 */
	private static final void _toString(Expression expr, Appendable dst) throws IOException {
		if(expr instanceof Expression) {
			if(Operator.AND.is(expr)) {
				_toString(OpAnd.getLeft(expr), dst);
				dst.append(" AND ");
				_toString(OpAnd.getRight(expr), dst);
			}
			else if(Operator.OR.is(expr)) {
				_toString(OpOr.getLeft(expr), dst);
				dst.append(" OR ");
				_toString(OpOr.getRight(expr), dst);
			}
			else if(Operator.EQUAL.is(expr)) {
				dst.append("==");
				dst.append(OpEqual.getEqual(expr).toString());
			}
			else if(Operator.GREATER.is(expr)) {
				dst.append(">");
				dst.append(OpGreater.getGreater(expr).toString());
			}
			else if(Operator.GREATER_OR_EQUAL.is(expr)) {
				dst.append(">=");
				dst.append(OpGreaterOrEqual.getGreaterOrEqual(expr).toString());
			}
			else if(Operator.LESS.is(expr)) {
				dst.append("<");
				dst.append(OpLess.getLess(expr).toString());
			}
			else if(Operator.LESS_OR_EQUAL.is(expr)) {
				dst.append("<=");
				dst.append(OpLessOrEqual.getLessOrEqual(expr).toString());
			}
			else if(Operator.NOT.is(expr)) {
				dst.append("!(");
				_toString(OpNot.getNot(expr), dst);
				dst.append(")");
			}
			else if(Operator.NOT_EQUAL.is(expr)) {
				dst.append("!");
				dst.append(OpNotEqual.getNotEqual(expr).toString());
			}
			else if(Operator.COMPOSITE.is(expr)) {
				_toString(OpComposite.getExpression(expr), dst);
			}
			else {
				_toString(expr, dst);
			}
		}
		else if(expr instanceof Version) {
			dst.append(((Version)expr).toString());
		}
		else {
			throw new IllegalArgumentException("unknown expression child type " + (expr != null ? expr.getClass() : "null"));
		}
	}

}
