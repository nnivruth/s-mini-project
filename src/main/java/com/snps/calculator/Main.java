package com.snps.calculator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static com.snps.calculator.Constant.INVALID_INPUT;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting calculator..");
        if (args.length < 1 || StringUtils.isBlank(args[0]) || args.length > 1) { //validates input args
            log.error("Invalid input..");
            System.out.println(INVALID_INPUT);
        } else {
            System.out.println(Application.getInstance().calculate(args[0]));
        }
    }

}
