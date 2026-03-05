package ast;

public class IfStatement extends Statement {
    final Condition condition;
    final Block thenBlock;
    final Block elseBlock; 
    
    public IfStatement(Condition condition, Block thenBlock, Block elseBlock, Location loc) {
        super(loc);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    //IfStatement(cond, thenStmt, elseStmt, loc) {
    // thenStmt and elseStmt can be blocks or single statements
    // Optionally, wrap single statements into Block if your AST expects a Block always
    //this.thenBlock = (thenStmt instanceof Block) ? thenStmt : new Block([thenStmt]);
    //this.elseBlock = (elseStmt instanceof Block) ? elseStmt : new Block([elseStmt]);
    //}

    public Condition getCondition() {
        return condition;
    }

    public Block getThenBlock() {
        return thenBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    public boolean hasElse() {
        return elseBlock != null;
    }
}