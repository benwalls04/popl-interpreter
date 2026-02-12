package ast;

public class Comparison extends Condition {
    public static final int EQUALS = 1;
    public static final int NOT_EQUALS = 2;
    public static final int LESS_THAN = 3;
    public static final int GREATER_THAN = 4;
    public static final int LESS_EQUAL = 5;
    public static final int GREATER_EQUAL = 6;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public Comparison(Expr e1, int op, Expr e2, Location loc) {
      super(loc);
      this.expr1 = e1;
      this.expr2 = e2;
      this.operator = op; 
    }

    public Expr getLeftExpr() {
      return expr1;
    }

    public Expr getRightExpr() {
      return expr2;
    }

    public int getOperator() {
      return operator;
    }

    @Override
    public String toString() {
      return expr1.toString() + " " + Integer.toString(operator) + " " + expr2.toString();  
    }
}