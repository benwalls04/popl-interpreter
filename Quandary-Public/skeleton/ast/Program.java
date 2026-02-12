package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final Block b;

    public Program(Block b, Location loc) {
        super(loc);
        this.b = b;
    }

    public Block getBlock() {
        return b;
    }

    public void println(PrintStream ps) {
        ps.println(b);
    }
}
