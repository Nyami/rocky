/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rockstar.expression;

/**
 *
 * @author Gabor
 */
public class DivideExpression extends CompoundExpression {

    @Override
    protected String getFormat() {
        return "(%s / %s)";
    }
    
}
