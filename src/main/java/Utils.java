import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

public class Utils {
    static boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (Boolean) object;
        return true;
    }

    static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    static void checkNumberOperands(ParserRuleContext ctx, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError("Operands must be two numbers.", getOriginalText(ctx), ctx.start.getLine());
    }

    static void checkNumberOperand(ParserRuleContext ctx, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError("Operand must be number.", getOriginalText(ctx), ctx.start.getLine());
    }

    static String getOriginalText(ParserRuleContext ctx) {
        int startIndex = ctx.start.getStartIndex();
        int stopIndex = ctx.stop != null ? ctx.stop.getStopIndex(): -1;
        return ctx.start.getTokenSource().getInputStream().getText(new Interval(startIndex, stopIndex));
    }
}
