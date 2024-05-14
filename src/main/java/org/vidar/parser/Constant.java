package org.vidar.parser;

public class Constant {

    /**
     * End of input character. Used as a sentinel to denote the character one beyond the last defined character in a
     * source file.
     */
    static final byte EOI = 0x1A;

    public static void main(String[] args) {
        System.out.println((char) 0x1A);
    }
}
