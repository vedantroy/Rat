import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Rat {
    private static boolean hadRuntimeError = false;


    public static void main(String[] args) throws IOException {
        InputStream is = System.in;
        if(args.length == 0) {
            System.out.println("No path to .rat file passed as argument. Type rat code below.");
            System.out.println("To finish type an EOF (Ctrl-D (Linux), Ctrl-Z-Enter (Windows))");
        } else {
            is = new FileInputStream(new File(args[0]));
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        RatLexer lexer = new RatLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RatParser parser = new RatParser(tokens);
        ParseTree tree = parser.program();

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(tree);
    }

    static void runtimeError(RuntimeError error) {
        if(error.line != -1) {
            System.err.println(error.getMessage() + "\n[line " + error.line + " at " + "\"" + error.context +  "\"" + "]");
        } else {
            System.err.println(error.getMessage() + "\n[At " + "\"" + error.context +  "\"" + "]");
        }
        hadRuntimeError = true;
    }
}
