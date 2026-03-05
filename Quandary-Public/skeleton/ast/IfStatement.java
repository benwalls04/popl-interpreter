package ast;
import java.util.ArrayList;
import java.util.Arrays;

public class IfStatement extends Statement {
    final Condition condition;
    final Block thenBlock;
    final Block elseBlock; 
    
    public IfStatement(Condition condition, Statement thenBlock, Statement elseBlock, Location loc){
        super(loc);
        this.condition = condition;
        this.thenBlock = (thenBlock instanceof Block) ? (Block) thenBlock 
                       : new Block(new ArrayList<>(Arrays.asList(thenBlock)), loc);
        this.elseBlock = (elseBlock instanceof Block) ? (Block) elseBlock
                       : (elseBlock != null ? new Block(new ArrayList<>(Arrays.asList(elseBlock)), loc) : null);
    }

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