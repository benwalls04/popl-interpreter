package ast;
 
public class CallStatement extends Statement {
 
    final FunctionCall call;
 
    public CallStatement(FunctionCall call, Location loc) {
        super(loc);
        this.call = call;
    }
 
    public FunctionCall getCall() {
        return call;
    }
 
    @Override
    public String toString() {
        return call.toString() + ";";
    }
}
 



