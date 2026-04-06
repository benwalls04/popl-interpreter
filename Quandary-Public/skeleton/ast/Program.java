package ast;

import java.io.PrintStream;
import java.util.ArrayList;

public class Program extends ASTNode {

    final ArrayList<FunctionDecl> functionList;

    public Program(ArrayList<FunctionDecl> functionList, Location loc) {
        super(loc);
        this.functionList = functionList;
    }

    public ArrayList<FunctionDecl> getFunctions() {
        return functionList;
    }

    public void println(PrintStream ps) {
        ps.println("program print");
    }
}
