/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.parser.checker;

import rockstar.expression.Expression;
import rockstar.parser.ExpressionFactory;
import rockstar.statement.Statement;
import rockstar.statement.WhileStatement;

/**
 *
 * @author Gabor
 */
public class WhileChecker extends Checker {
    
    @Override
    public Statement check() {
        if (match("While", 1)) {
            Expression condition = ExpressionFactory.getExpressionFor(getResult()[1], line);
            if (condition != null) {
                return new WhileStatement(condition);
            }
        }
        return null;
    }
    
}
