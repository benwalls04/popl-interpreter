package ast;

public class BlockStatement extends Statement {

    final Block block;

    public BlockStatement(Block block, Location loc) {
        super(loc);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
