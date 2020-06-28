package com.snps.calculator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.snps.calculator.Constant.ADD;
import static com.snps.calculator.Constant.CLOSE_P;
import static com.snps.calculator.Constant.COMMA;
import static com.snps.calculator.Constant.DIV;
import static com.snps.calculator.Constant.INVALID_INPUT;
import static com.snps.calculator.Constant.LET;
import static com.snps.calculator.Constant.MULT;
import static com.snps.calculator.Constant.OPEN_P;
import static com.snps.calculator.Constant.SUB;

class Application {

    private Application() {
        // private constructor
    }

    private static Application instance;

    static Application getInstance() {
        if (Objects.isNull(instance)) {
            instance = new Application();
        }
        return instance;
    }

    // stores values of variables in "let" expressions
    private static Map<String, BigInteger> letVariableMap = new HashMap<>();

    private static final Logger log = LogManager.getLogger(Application.class);


    /**
     * Calculates value as {@link String} from input expression.
     *
     * @param input input expression
     * @return calculated value in string format
     */
    String calculate(String input) {
        BigInteger result = null;
        try {
            input = StringUtils.deleteWhitespace(input); //removes all whitespaces
            validateParentheses(input);
            log.info("Calculating value for " + input);
            result = calculate(0, input.toCharArray()); //starts with 0 start index & input as char[]
        } catch (Exception e) {
            log.error("Exception while calculating ", e);
        }
        return result != null ? result.toString() : INVALID_INPUT;
    }

    /**
     * Validates if parentheses' exist in expression & are balanced
     * '('s should be equal to ')'s
     *
     * @param expr input expression
     */
    private void validateParentheses(String expr) {
        log.debug("Validating if parentheses are balanced..");
        int open = 0; //number of open/left parentheses
        int close = 0; //number of closed/right parentheses
        for (char c : expr.toCharArray()) {
            if (c == OPEN_P) {
                open++;
            } else if (c == CLOSE_P) {
                close++;
            }
        }
        if (open == 0 || close == 0 || open != close) { //parentheses' missing/mismatch
            throw new IllegalArgumentException("Invalid expression " + expr);
        }
    }

    /**
     * Calculates value as {@link BigInteger} from index & expression
     * This function is recursively called (if needed) as input expression is evaluated
     *
     * @param idx  index
     * @param expr expression as char[]
     * @return calculated value
     */
    private BigInteger calculate(int idx, char[] expr) {
        BigInteger intVal1;
        BigInteger intVal2;
        String op = getExpr(idx, OPEN_P, expr); //gets operation
        log.debug("Operation is " + op);
        idx = idx + op.length() + 1; //updates index
        if (op.equals(LET)) {
            return evaluateLetExpr(idx, expr);
        } else {
            String val1 = getExpr(idx, COMMA, expr);
            if (letVariableMap.containsKey(val1)) {
                intVal1 = letVariableMap.get(val1); //gets value from map, if exists
            } else {
                intVal1 = isNumber(val1) ? new BigInteger(val1) : calculate(idx, expr); //calls recursively if val1 isn't a number
            }
            idx = idx + val1.length() + 1; //updates index
            String val2 = getExpr(idx, CLOSE_P, expr);
            if (letVariableMap.containsKey(val2)) {
                intVal2 = letVariableMap.get(val2); //gets value from map, if exists
            } else {
                intVal2 = isNumber(val2) ? new BigInteger(val2) : calculate(idx, expr); //calls recursively if val2 isn't a number
            }
        }
        log.debug("Values to be evaluated are " + intVal1 + ", " + intVal2);
        switch (op) {
            case DIV:
                return intVal1.divide(intVal2);
            case MULT:
                return intVal1.multiply(intVal2);
            case ADD:
                return intVal1.add(intVal2);
            case SUB:
                return intVal1.subtract(intVal2);
            default:
                throw new IllegalArgumentException("Invalid operation " + op);
        }
    }

    /**
     * Gets substring of expression till delimiter
     *
     * @param idx   index
     * @param delim delimiter
     * @param expr  expression
     * @return substring
     */
    private String getExpr(int idx, char delim, char[] expr) {
        log.debug("Getting expression..");
        StringBuilder exprB = new StringBuilder();
        int open = 0; //number of open/left parentheses
        int close = 0; //number of closed/right parentheses
        for (int i = idx; i < expr.length; i++) {
            exprB.append(expr[i]);
            if (expr[i] == OPEN_P) {
                open++;
            } else if (expr[i] == CLOSE_P) {
                close++;
            }
            if (expr[i] == delim && (delim != COMMA || open == close)) { //extracts complete expression till delimiter & parentheses balance
                log.debug("End of expression..");
                break;
            }
        }
        if (exprB.length() == 0) {
            throw new IllegalArgumentException("Invalid expression " + new String(expr));
        }
        exprB.setLength(exprB.length() - 1); //removes delimiter from expression
        String exprS = exprB.toString();
        log.debug("Substring is " + exprS);
        return exprS;
    }

    /**
     * Checks if the {@link String} value is an integer or not
     * {@link NumberFormatException} is thrown if value is not an {@link Integer}
     *
     * @param val value in string format
     * @return true/false
     */
    private boolean isNumber(String val) {
        try {
            log.debug("Checking if " + val + " is an integer or not");
            new BigInteger(val);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Evaluates "let" expression as {@link BigInteger} from index & expression
     *
     * @param idx  index
     * @param expr expression
     * @return calculated value
     */
    private BigInteger evaluateLetExpr(int idx, char[] expr) {
        log.debug("Evaluating \"let\" expression..");
        String var = getExpr(idx, COMMA, expr);
        idx = idx + var.length() + 1; //updates index
        String val = getExpr(idx, COMMA, expr);
        idx = idx + val.length() + 1; //updates index
        //updates map if val is numeric, else calls calculate function to resolve expression further
        letVariableMap.put(var, isNumber(val) ? new BigInteger(val) : calculate(0, val.toCharArray()));
        //gets final expression within "let" expression to be evaluated
        return calculate(0, getLetExpr(idx, expr).toCharArray());
    }

    /**
     * Gets "let" expression from index & expression
     *
     * @param idx  index
     * @param expr expression
     * @return "let" expression
     */
    private String getLetExpr(int idx, char[] expr) {
        log.debug("Getting \"let\" expression..");
        StringBuilder letExprB = new StringBuilder();
        int open = 0; //number of open/left parentheses
        int close = 0; //number of closed/right parentheses
        for (int i = idx; i < expr.length; i++) {
            letExprB.append(expr[i]);
            if (expr[i] == OPEN_P) {
                open++;
            } else if (expr[i] == CLOSE_P) {
                close++;
                if (open == close) {
                    log.debug("End of \"let\" expression..");
                    break;
                }
            }
        }
        String letExpr = letExprB.toString();
        log.debug("\"let\" expression is " + letExpr);
        return letExpr;
    }

}
