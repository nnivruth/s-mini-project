package com.snps.calculator;

import org.junit.Test;

import static com.snps.calculator.Constant.INVALID_INPUT;
import static org.junit.Assert.assertEquals;

public class ApplicationTest {

    private Application application = Application.getInstance();

    @Test
    public void testCalculate() {
        assertEquals("3", application.calculate("add(1, 2)"));
        assertEquals("-5", application.calculate("sub(1, mult(2, 3))"));
        assertEquals("12", application.calculate("mult(add(2, 2), div(9, 3))"));
        assertEquals("10", application.calculate("let(a, 5, add(a, a))"));
        assertEquals("55", application.calculate("let(a, 5, let(b, mult(a, 10), add(b, a)))"));
        assertEquals("40", application.calculate("let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))"));
    }

    @Test
    public void testCalculate_invalid() {
        assertEquals(INVALID_INPUT, application.calculate("abc"));
    }

    @Test
    public void testCalculate_incomplete() {
        assertEquals(INVALID_INPUT, application.calculate("add(1, )"));
    }

    @Test
    public void testCalculate_parenthesesMismatch() {
        assertEquals(INVALID_INPUT, application.calculate("add(1, 2))"));
    }

    @Test
    public void testCalculate_invalidOp() {
        assertEquals(INVALID_INPUT, application.calculate("subs(1, 2)"));
    }

}
