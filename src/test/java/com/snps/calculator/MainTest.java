package com.snps.calculator;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;

public class MainTest extends TestCase {

    public MainTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        return new TestSuite(MainTest.class);
    }

    @Test
    public void test() {
        Main.main(new String[]{"add(1, 2)"});
    }

    @Test
    public void testWoArgs() {
        Main.main(new String[]{});
    }

}
