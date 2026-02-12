package ast;

public class ReturnStatement extends Statement {

    final Expr expr;

    public ReturnStatement(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "return " + expr.toString() + ";";
    }
}
