package ast;

public class IfStatement extends Statement {
    final Block block;
    final Condition cond;

    public IfStatement(Block block, Condition cond, Location loc) {
        super(loc);
        this.block = block;
        this.cond = cond;
    }

    public Block getBlock() {
        return block;
    }

    public Condition getCondition() {
        return cond;
    }
}