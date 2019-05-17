/*
public class Interpreter2 extends RatBaseVisitor<Object> {

    @Override
    public Object visitExpression(RatParser.ExpressionContext ctx) {
        if(ctx.op == null || ctx.op.getText() == null) {
            return visit(ctx.primary());
        }
        switch (ctx.op.getText()) {
            case "==":
                System.out.println("Equality index/alt: " + ctx.getRuleIndex() + " " + ctx.getAltNumber());
                Object left = visit(ctx.expression(0));
                Object right = visit(ctx.expression(1));
                return left == right;
            case "+":
                System.out.println("+ index/alt: " + ctx.getRuleIndex() + " " + ctx.getAltNumber());
                Object left2 = visit(ctx.expression(0));
                Object right2 = visit(ctx.expression(1));
                return (double) left2 + (double) right2;
        }
        System.out.println(ctx.getRuleIndex());
        return null;
    }


    @Override
    public Object visitIntLiteral(RatParser.IntLiteralContext ctx) {
        System.out.println("Integer index/alt: " + ctx.getRuleIndex() + " " + ctx.getAltNumber());
        return Double.parseDouble(ctx.getText());
    }
}
*/