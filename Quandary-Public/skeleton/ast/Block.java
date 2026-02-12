package ast;
import java.util.ArrayList;

public class Block extends ASTNode{
  final ArrayList<Statement> statements;

  public Block(ArrayList<Statement> statements, Location loc) {
    super(loc);
    this.statements = statements;
  }

  public ArrayList<Statement> getStatements() {
    return statements;
  }
}