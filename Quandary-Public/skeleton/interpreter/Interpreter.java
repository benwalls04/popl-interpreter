package interpreter;

import java.io.*;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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

        Map<String, FunctionDecl> functions = new HashMap<>();

        for (FunctionDecl f : astRoot.getFunctions()) {
            functions.put(f.getName(), f);
        }

        if (!functions.containsKey("main")) {
            return "Error: main method not defined";
        }

        FunctionDecl mainFunc = functions.get("main");

        Map<String, Object> memory = new HashMap<>();
        memory.put(mainFunc.getParams().get(0), arg);

        return executeStmtList(mainFunc.getBody(), memory, functions);
    }    
    Object executeBlock(Block block, Map<String, Object> memory, Map<String, FunctionDecl> functions) {
        return executeStmtList(block.getStatements(), memory, functions); 
    }

    Object executeStmtList(ArrayList<Statement> stmtList, Map<String, Object> memory, Map<String, FunctionDecl> functions) {
        for (Statement statement : stmtList) {
            Object result = executeStatement(statement, memory, functions);
            if (result != null) {
                return result; 
            }
        }
        return null; 
    }

    Object executeStatement(Statement statement, Map<String, Object> memory, Map<String, FunctionDecl> functions) {
        if (statement instanceof ReturnStatement) {
            return evaluateExpr(((ReturnStatement)statement).getExpr(), memory, functions);
        } else if (statement instanceof PrintStatement) {
            System.out.println(evaluateExpr(((PrintStatement)statement).getExpr(), memory, functions).toString());
        } else if (statement instanceof IfStatement) {
            Object result = executeIfStatement((IfStatement)statement, memory, functions);
            if (result != null) {
                return result; 
            }
        } else if (statement instanceof Declaration) {
            Declaration d = (Declaration)statement;
            memory.put(d.getName(), d.hasExpr()? evaluateExpr(d.getExpr(), memory, functions) : null);
        } else if (statement instanceof Assignment) {
            Assignment a = (Assignment)statement;
            if (memory.containsKey(a.getName())) {
                memory.put(a.getName(), evaluateExpr(a.getExpr(), memory, functions));
            }
        } else if (statement instanceof Block) {
            Block blockStmt = (Block)statement;
            Object result = executeBlock(blockStmt, memory, functions);
            if (result != null) {
                return result;
            }

        }
        return null;
    }

    
    Object executeIfStatement(IfStatement statement, Map<String, Object> memory, Map<String, FunctionDecl> functions) {
        if (evaluateCondition(statement.getCondition(), memory, functions)) {
            Object result = executeBlock(statement.getThenBlock(), memory, functions);
            if (result != null) {  
                return result;
            }
        } else if (statement.hasElse()) {
            Object result = executeBlock(statement.getElseBlock(), memory, functions);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    Boolean evaluateCondition(Condition cond, Map<String, Object> memory, Map<String, FunctionDecl> functions) {
        if (cond instanceof Comparison) {
            Comparison comp = (Comparison)cond;
            long val1 = (Long)evaluateExpr(comp.getLeftExpr(), memory, functions);
            long val2 = (Long)evaluateExpr(comp.getRightExpr(), memory, functions);
            
            switch(comp.getOperator()) {
                case Comparison.EQUALS: return val1 == val2;
                case Comparison.NOT_EQUALS: return val1 != val2;
                case Comparison.LESS_THAN: return val1 < val2;
                case Comparison.GREATER_THAN: return val1 > val2;
                case Comparison.LESS_EQUAL: return val1 <= val2;
                case Comparison.GREATER_EQUAL: return val1 >= val2;
                default: throw new RuntimeException("Unknown operator");
            }
        } else if (cond instanceof LogicalCond) {
            LogicalCond logic = (LogicalCond)cond;
            if (logic.getOperator() == LogicalCond.NOT) {
                return !evaluateCondition(logic.getLeftCond(), memory, functions);
            } 
            if (logic.getOperator() == LogicalCond.OR) {
                Boolean val1 = evaluateCondition(logic.getLeftCond(), memory, functions);
                if (val1) return true; 
                return evaluateCondition(logic.getRightCond(), memory, functions);
            } else if (logic.getOperator() == LogicalCond.AND) {
                Boolean val1 = evaluateCondition(logic.getLeftCond(), memory, functions);
                if (!val1) return false; 
                return evaluateCondition(logic.getRightCond(), memory, functions);
            }
        }
        throw new RuntimeException("Unknown condition type");
    }

    Object evaluateExpr(Expr expr, Map<String, Object> memory, Map<String, FunctionDecl> functions) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr)expr).getValue();
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            long val1 = (Long)evaluateExpr(binaryExpr.getLeftExpr(), memory, functions);
            long val2 = (Long)evaluateExpr(binaryExpr.getRightExpr(), memory, functions);
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return val1 + val2;
                case BinaryExpr.MINUS: return val1 - val2;
                case BinaryExpr.MULT: return val1 * val2;
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryMinusExpr) {
            UnaryMinusExpr unaryMinusExpr = (UnaryMinusExpr)expr;
            return -1 * (Long)evaluateExpr(unaryMinusExpr.getExpr(), memory, functions);
        } else if (expr instanceof VarExpr) {
            VarExpr varExpr = (VarExpr)expr;
            String varName = varExpr.getName();
            if (!memory.containsKey(varName)) {
                throw new RuntimeException("Undefined variable: " + varName);
            }
            return memory.get(varName);
        } else if (expr instanceof FunctionCall) {
            FunctionCall funcCall = (FunctionCall) expr; 
            // hashmap populated when interpreter is first created, go through function list 
            FunctionDecl calleeDef = functions.get(funcCall.getName());
            // built in function 
            if (calleeDef == null) {
                if (funcCall.getName().equals("randomInt")) {
                    int n;
                    if (funcCall.getArgs().isEmpty()) {
                        n = 100; 
                    } else {
                        Object argVal = evaluateExpr(funcCall.getArgs().get(0), memory, functions); 
                        n = ((Long) argVal).intValue();                   
                    }
                    return (long) random.nextInt(n);
                } else {
                    return null; 
                }

            }
            HashMap<String, Object> newEnv = new HashMap<String, Object>();
            ArrayList<Expr> args = funcCall.getArgs();
            ArrayList<String> params = calleeDef.getParams();

            if (args.size() > params.size()) {
                return "Error: more arguments given than expected";
            }

            for (int i = 0; i < args.size(); i++) {
                newEnv.put(params.get(i), evaluateExpr(args.get(i), memory, functions));
            }
            
            return executeStmtList(calleeDef.getBody(), newEnv, functions);
        }
        else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
