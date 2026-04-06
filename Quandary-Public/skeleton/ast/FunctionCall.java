package ast;
import java.util.ArrayList;

public class FunctionCall extends Expr {

    final String name; 
    final ArrayList<Expr> args;

    public FunctionCall(String name, ArrayList<Expr> args, Location loc) {
        super(loc);
        this.name = name;
        this.args = args;
    }

    public ArrayList<Expr> getArgs() {
        return args; 
    }

    public String getName() {
        return name;
    }

  @Override
    public String toString() {
      ArrayList<String> argStrings = new ArrayList<>();

      for (Expr e : args) {
          argStrings.add(e.toString());
      }

      return name + "(" + String.join(", ", argStrings) + ")";
    }
}
