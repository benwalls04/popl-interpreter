package ast;

public class LogicalCond extends Condition {
    public static final int NOT = 1;
    public static final int AND = 2;
    public static final int OR = 3;

    final Condition cond1;
    final int operator;
    final Condition cond2;

    public LogicalCond(Condition c1, int op, Condition c2, Location loc) {
      super(loc);
      this.cond1 = c1;
      this.cond2 = c2;
      this.operator = op; 
    }

    public Condition getLeftCond() {
      return cond1;
    }

    public Condition getRightCond() {
      return cond2;
    }

    public int getOperator() {
      return operator;
    }

    @Override
    public String toString() {
        switch (operator) {
            case NOT:
                return "!" + cond1.toString();
            case AND:
                return cond1.toString() + " && " + cond2.toString();
            case OR:
                return cond1.toString() + " || " + cond2.toString();
            default:
                return "unknown operator";
        }
    }

}