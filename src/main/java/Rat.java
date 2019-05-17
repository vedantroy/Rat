import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Rat {
    private static boolean hadRuntimeError = false;


    public static void main(String[] args) throws IOException {
        String inputFile = "src/test/rat/tests.rat";
        //if ( args.length > 0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!= null ) is = new FileInputStream(inputFile);
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
