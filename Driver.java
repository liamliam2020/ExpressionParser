import java.util.*;

public class Driver {
	public static void main(String[] args) {
		Map<String, Double> vars = new HashMap<>();
		// you can change or add more variables here.
		vars.put("x", 10.0);
		vars.put("y", 27.0);
		vars.put("z", 123.1);

		Expression expr = Expression.quickParse("4*x + y/9 + 12");

		System.out.println("toString:        " + expr);
		System.out.println("toPostfix:       " + expr.toPostfix());
		System.out.println("evaluate:        " + expr.evaluate(vars));
		System.out.println("reciprocal:      " + expr.reciprocal());
		System.out.println("reciprocal(num): " + Expression.Number(7).reciprocal());
		System.out.println("reciprocal(div): " + Expression.quickParse("x / 10").reciprocal());
		System.out.println("getVariables:    " + expr.getVariables());

		Expression mean = Expression.geometricMean(new double[]{4, 9, 3, 7, 6});
		System.out.println("geometricMean:   " + mean);
		System.out.println("it evalutes to:  " + mean.evaluate(vars));

		System.out.println("===================================================");
		System.out.println("NOW I TEST MORE THOROUGHLY :)");

		Expression exprz = Expression.quickParse("4^x + z/9 + y");

		System.out.println("toString:        " + exprz);
		System.out.println("toPostfix:       " + exprz.toPostfix());
		System.out.println("evaluate:        " + exprz.evaluate(vars));
		System.out.println("reciprocal:      " + exprz.reciprocal());
		System.out.println("reciprocal(num): " + Expression.Number(13).reciprocal());
		System.out.println("reciprocal(div): " + Expression.quickParse("x*2 / 13").reciprocal());
		System.out.println("getVariables:    " + exprz.getVariables());

		Expression meanz = Expression.geometricMean(new double[]{14, 2, 3, 7, 9, 16, 11});
		System.out.println("geometricMean:   " + meanz);
		System.out.println("it evalutes to:  " + meanz.evaluate(vars));
	}
}