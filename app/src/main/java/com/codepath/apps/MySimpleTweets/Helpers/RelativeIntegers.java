package com.codepath.apps.MySimpleTweets.Helpers;

import java.text.DecimalFormat;

// Formats the integers to K thousand, M millions, B billions etc.
public class RelativeIntegers {
    public static void main(String args[]) {
        long[] numbers = new long[]{7, 12, 856, 1000, 5821, 10500, 101800, 2000000, 7800000, 92150000, 123200000, 9999999};
        for(long number : numbers) {
            System.out.println(number + " = " + format(number));
        }
    }

    private static String[] suffix = new String[]{"","k", "m", "b", "t"};
    private static int MAX_LENGTH = 4;

    public static String format(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while(r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")){
            r = r.substring(0, r.length()-2) + r.substring(r.length() - 1);
        }
        return r;
    }
}
