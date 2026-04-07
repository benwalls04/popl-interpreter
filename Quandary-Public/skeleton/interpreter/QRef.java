package interpreter;

public class QRef {
    public Object left;
    public Object right;

    public QRef(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + valueToString(left) + " . " + valueToString(right) + ")";
    }

    private String valueToString(Object val) {
        if (val == Interpreter.NIL) return "nil";
        return val.toString();
    }
}