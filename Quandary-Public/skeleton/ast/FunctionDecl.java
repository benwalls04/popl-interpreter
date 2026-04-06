package ast;
import java.util.ArrayList; 

public class FunctionDecl extends ASTNode {
  final String name;
  final ArrayList<String> params; 
  final ArrayList<Statement> body; 

  public FunctionDecl(String name, ArrayList<String> params, ArrayList<Statement> body, Location loc) {
    super(loc);
    this.name = name; 
    this.params = params; 
    this.body = body; 
  }

  public String getName() {
    return name;
  }

  public ArrayList<String> getParams() {
    return params; 
  }

  public ArrayList<Statement> getBody() {
    return body;
  }
}