package interpreter;

import java.io.*;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        //astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    Object executeRoot(Program astRoot, long arg) {
        return executeBlock(astRoot.getBlock());
    }

    Object executeBlock(Block block) {
        for (Statement statement : block.getStatements()) {
            Object result = executeStatement(statement);
            if (statement instanceof ReturnStatement) {
                return result; 
            }
        }
        return null;  
    }

    Object executeStatement(Statement statement) {
        if (statement instanceof ReturnStatement) {
            return evaluateExpr(((ReturnStatement)statement).getExpr());
        } else if (statement instanceof PrintStatement) {
            System.out.println(evaluateExpr(((PrintStatement)statement).getExpr()).toString());
        } else if (statement instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement)statement; 
            if (evaluateCondition(ifStatement.getCondition())) {
                executeBlock(ifStatement.getBlock());
            }
        }
        return null;
    }

    Boolean evaluateCondition(Condition cond) {
        if (cond instanceof Comparison) {
            Comparison comp = (Comparison)cond;
            long val1 = (Long)evaluateExpr(comp.getLeftExpr());
            long val2 = (Long)evaluateExpr(comp.getRightExpr());
            
            switch(comp.getOperator()) {
                case Comparison.EQUALS: return val1 == val2;
                case Comparison.NOT_EQUALS: return val1 != val2;
                case Comparison.LESS_THAN: return val1 < val2;
                case Comparison.GREATER_THAN: return val1 > val2;
                case Comparison.LESS_EQUAL: return val1 <= val2;
                case Comparison.GREATER_EQUAL: return val1 >= val2;
                default: throw new RuntimeException("Unknown operator");
            }
        }
        throw new RuntimeException("Unknown condition type");
    }

    Object evaluateExpr(Expr expr) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr)expr).getValue();
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            long val1 = (Long)evaluateExpr(binaryExpr.getLeftExpr());
            long val2 = (Long)evaluateExpr(binaryExpr.getRightExpr());
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return val1 + val2;
                case BinaryExpr.MINUS: return val1 - val2;
                case BinaryExpr.MULT: return val1 * val2;
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryMinusExpr) {
            UnaryMinusExpr unaryMinusExpr = (UnaryMinusExpr)expr;
            return -1 * (Long)evaluateExpr(unaryMinusExpr.getExpr());
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
