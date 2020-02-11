package org.soraworld.violet.util;

/**
 * @author Himmelt
 */
public final class Maths {
    public static int floor(double num) {
        int tmp = (int) num;
        if (num < tmp) {
            return tmp - 1;
        }
        return tmp;
    }
}
