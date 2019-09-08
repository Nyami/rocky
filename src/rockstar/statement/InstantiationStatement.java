/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.statement;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import rockstar.expression.Expression;
import rockstar.expression.VariableReference;
import rockstar.runtime.BlockContext;
import rockstar.runtime.RockstarRuntimeException;
import rockstar.runtime.Value;

/**
 *
 * @author Gabor
 */
public class InstantiationStatement extends Statement {

    private final VariableReference variable;
    private final String className;
    private final List<Expression> ctorParameterExprs = new ArrayList<>();

    public InstantiationStatement(VariableReference variable, String className) {
        this.variable = variable;
        this.className = className;
    }

    public void addParameter(Expression expr) {
        ctorParameterExprs.add(expr);
    }

    @Override
    public void execute(BlockContext ctx) {
        ClassBlock block = ctx.retrieveClass(className);
        if (block != null) {
            List paramValues = new LinkedList();
            ctorParameterExprs.forEach((expr) -> {
                paramValues.add(expr.evaluate(ctx));
            });
            Value instance = block.instantiate(ctx, paramValues);
            ctx.setVariable(this.variable, instance);
        } else {
            throw new RockstarRuntimeException("Undefined class: " + className);
        }
    }

    @Override
    protected String explain() {
        String paramsList = ctorParameterExprs.toString();
        return variable.format() + " := new " + className + "(" + paramsList.substring(1, paramsList.length() - 1) + ")";
    }

}
