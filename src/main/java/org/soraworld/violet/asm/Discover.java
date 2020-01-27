package org.soraworld.violet.asm;

import java.util.regex.Pattern;

/**
 * @author Himmelt
 */
public class Discover {
    public static Pattern classFile = Pattern.compile("[^\\s$]+(\\$[^\\s]+)?\\.class$");
}
