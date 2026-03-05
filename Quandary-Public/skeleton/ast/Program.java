package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final Block b;
    final String arg;

    public Program(String arg, Block b, Location loc) {
        super(loc);
        this.b = b;
        this.arg = arg;
    }

    public Block getBlock() {
        return b;
    }

    public String getArg() {
        return arg;
    }

    public void println(PrintStream ps) {
        ps.println(b);
    }
}
