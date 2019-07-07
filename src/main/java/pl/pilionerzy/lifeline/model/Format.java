package pl.pilionerzy.lifeline.model;

import java.text.DecimalFormat;

class Format {

    private static final DecimalFormat format;

    static {
        format = new DecimalFormat("0%");
        format.setMultiplier(1);
    }

    static String asPercentage(int number) {
        return format.format(number);
    }
}
