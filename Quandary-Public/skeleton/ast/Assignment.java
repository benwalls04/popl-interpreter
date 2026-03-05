package ast;

public class Assignment extends Statement {

  final String name; 
  final Expr expr;  

  public Assignment(String name, Expr expr, Location loc) {
    super(loc);
    this.name = name; 
    this.expr = expr; 
  }

  public String getName() {
    return name;
  }

  public Expr getExpr() {
    return expr; 
  }

  @Override
  public String toString() {
    return name + " = " + expr.toString();
  }
}