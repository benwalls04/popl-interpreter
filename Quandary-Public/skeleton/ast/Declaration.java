package ast;

public class Declaration extends Statement {
  final String name; 
  final Expr expr;  

  public Declaration(String name, Expr expr, Location loc) {
    super(loc);
    this.name = name; 
    this.expr = expr; 
  }

  public String getName() {
    return name;
  }

  public boolean hasExpr() {
      return expr != null;
  }

  public Expr getExpr() {
    return expr; 
  }

  @Override
  public String toString() {
    return name + " = " + expr.toString();
  }
}