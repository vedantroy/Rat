import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.rmi.CORBA.Util;
import java.util.List;

public class Interpreter extends RatBaseVisitor<Object> {
    private Environment environment = new Environment();

    void interpret(ParseTree tree) {
        try {
            visit(tree);
        } catch (RuntimeError error) {
            Rat.runtimeError(error);
        }
    }

    @Override
    public Object visitVarDeclaration(RatParser.VarDeclarationContext ctx) {
        Object value = null;
        if(ctx.expression() != null) {
            value = visit(ctx.expression());
        }

        environment.define(ctx.IDENTIFIER().toString(), value);
        return null;
    }

    @Override
    public Object visitIfStmt(RatParser.IfStmtContext ctx) {
        if(Utils.isTruthy(visit(ctx.expression()))) {
            visit(ctx.statement(0));
        } else if (ctx.statement().size() == 2) {
            visit(ctx.statement(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(RatParser.WhileStmtContext ctx) {
        while (Utils.isTruthy(visit(ctx.expression()))) {
            visit(ctx.statement());
        }
        return null;
    }

    @Override
    public Object visitPrintStmt(RatParser.PrintStmtContext ctx) {
        Object value = visit(ctx.expression());
        System.out.println(value);
        return null;
    }

    @Override
    public Object visitAssertStmt(RatParser.AssertStmtContext ctx) {
        Object value = visit(ctx.expression());
        if(!Utils.isTruthy(value)) {
            throw new RuntimeError("Assertion failed.", Utils.getOriginalText(ctx), ctx.start.getLine());
        }
        return null;
    }

    @Override
    public Object visitBlock(RatParser.BlockContext ctx) {
        executeBlock(ctx.declaration(), new Environment(environment));
        return null;
    }

    private void executeBlock(List<RatParser.DeclarationContext> declarations, Environment environment) {
        Environment previous = this.environment;
        this.environment = environment;

        for(RatParser.DeclarationContext declaration : declarations) {
            visit(declaration);
        }

        this.environment = previous;
    }

    @Override
    public Object visitAssignment(RatParser.AssignmentContext ctx) {
        if(ctx.assignment() != null) {
            Object value = visit(ctx.assignment());
            environment.assign(ctx.IDENTIFIER().getText(), value);
            return value;
        }
        return visitLogical_or(ctx.logical_or());
    }

    @Override
    public Object visitLogical_or(RatParser.Logical_orContext ctx) {
        if(ctx.logical_and().size() == 2) {
            Object left = visitLogical_and(ctx.logical_and(0));
            if(Utils.isTruthy(left)) {
                return left;
            }
            return visitLogical_and(ctx.logical_and(1));
        }
        return visitLogical_and(ctx.logical_and(0));
    }

    @Override
    public Object visitLogical_and(RatParser.Logical_andContext ctx) {
        if(ctx.equality().size() == 2) {
            Object left = visitEquality(ctx.equality(0));
            if(!Utils.isTruthy(left)) {
                return left;
            } else {
                return visitEquality(ctx.equality(1));
            }
        }
        return visitEquality(ctx.equality(0));
    }

    @Override
    public Object visitEquality(RatParser.EqualityContext ctx) {
        if(ctx.comparison().size() == 2) {
            boolean isEqual = Utils.isEqual(visit(ctx.comparison(0)), visit(ctx.comparison(1)));
            return ctx.op.getText().equals("==") == isEqual;
        }
        return visitComparison(ctx.comparison(0));
    }

    @Override
    public Object visitComparison(RatParser.ComparisonContext ctx) {
        if(ctx.addition().size() == 2) {
            Object left = visitAddition(ctx.addition(0));
            Object right = visitAddition(ctx.addition(1));

            switch (ctx.op.getText()) {
                case ">":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left > (double) right;
                case ">=":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left >= (double) right;
                case "<":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left < (double) right;
                case "<=":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left <= (double) right;
                default:
                    return null;
            }
        }
        return visitAddition(ctx.addition(0));
    }

    @Override
    public Object visitAddition(RatParser.AdditionContext ctx) {
        //TODO: The usage of .size == 2 means that addition is restricted to a = 1 + 2;
        if(ctx.multiplication().size() >= 2) {
            Object left = visitMultiplication(ctx.multiplication(0));
            Object right = visitMultiplication(ctx.multiplication(1));

            switch (ctx.op.getText()) {
                case "+":
                    if(left instanceof Double && right instanceof Double) {
                        return (double) left + (double) right;
                    }

                    if(left instanceof String && right instanceof String) {
                        System.out.println((String) left + (String) right);
                        return (String) left + (String) right;
                    }

                    throw new RuntimeError("Operands must be two numbers or two strings", Utils.getOriginalText(ctx), ctx.start.getLine());
                case "-":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left - (double) right;
                default:
                    return null;
            }
        }
        return visitMultiplication(ctx.multiplication(0));
    }

    @Override
    public Object visitMultiplication(RatParser.MultiplicationContext ctx) {
        if(ctx.unary().size() == 2) {
            Object left = visitUnary(ctx.unary(0));
            Object right = visitUnary(ctx.unary(1));

            switch (ctx.op.getText()) {
                case "*":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left * (double) right;
                case "/":
                    Utils.checkNumberOperands(ctx, left, right);
                    return (double) left / (double) right;
                default:
                    return null;
            }
        }
        return visitUnary(ctx.unary(0));
    }

    @Override
    public Object visitUnary(RatParser.UnaryContext ctx) {
        if(ctx.unary() != null) {
            Object operand = visitUnary(ctx.unary());
            switch (ctx.op.getText()) {
                case "-":
                    Utils.checkNumberOperand(ctx, operand);
                    return -(double)operand;
                case "!":
                    return !Utils.isTruthy(operand);
                default:
                    return null;
            }
        }
        return visitPrimary(ctx.primary());
    }

    @Override
    public Object visitNilLiteral(RatParser.NilLiteralContext ctx) {
        return null;
    }

    @Override
    public Object visitIntLiteral(RatParser.IntLiteralContext ctx) {
        return Double.parseDouble(ctx.NUMBER().getText());
    }

    @Override
    public Object visitBooleanLiteral(RatParser.BooleanLiteralContext ctx) {
        return ctx.value.getText().equals("True");
    }

    @Override
    public Object visitStringLiteral(RatParser.StringLiteralContext ctx) {
        String literal = ctx.STRING().getText();
        return literal.substring(1, literal.length() - 1);
    }

    @Override
    public Object visitIdentifier(RatParser.IdentifierContext ctx) {
        return environment.get(ctx.IDENTIFIER().getText());
    }

    @Override
    public Object visitGroup(RatParser.GroupContext ctx) {
        return visit(ctx.expression());
    }
}

