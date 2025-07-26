package com.globalpayex.utils;

public class Series {

    public static String generate(int n) {
        if (n < 2) {
            // throw an exception (object) to the caller (return)
            // Built in exception classes ---> IllegalArgumentException
            throw new IllegalArgumentException("n cannot be less than 2");
        }

        int a = 0;
        int b = 1;
        String result = "";
        result += a + "," + b;

        int i = 2;
        while (i < n) {
            int c = a + b;
            result += "," + c;
            a = b;
            b = c;
            i++;
        }

        // return  a value (return)
        return  result;
    }
}
