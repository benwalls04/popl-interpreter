package ast;
 
public class WhileStatement extends Statement {
 
    final Condition condition;
    final Statement body;
 
    public WhileStatement(Condition condition, Statement body, Location loc) {
        super(loc);
        this.condition = condition;
        this.body = body;
    }
 
    public Condition getCondition() {
        return condition;
    }
 
    public Statement getBody() {
        return body;
    }
 
    @Override
    public String toString() {
        return "while (" + condition.toString() + ") " + body.toString();
    }
}
 