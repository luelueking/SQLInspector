package org.vidar.parser;

import static org.vidar.parser.Constant.EOI;

/**
 * 字符类型
 */
public class CharType {

    private static final boolean[] whitespaceFlags = new boolean[256];

    static {
        for (int i = 0; i <= 32; ++i) {
            whitespaceFlags[i] = true;
        }

        whitespaceFlags[EOI] = false;
        for (int i = 0x7F; i <= 0xA0; ++i) {
            whitespaceFlags[i] = true;
        }

        whitespaceFlags[160] = true; // 特别处理
//        whitespaceFlags[223] = true; // 特别处理, odps
//        whitespaceFlags[229] = true; // 特别处理, odps
//        whitespaceFlags[231] = true; // 特别处理, odps ç
    }

    public static boolean isWhitespace(char c) {
        return (c <= whitespaceFlags.length && whitespaceFlags[c])
                || c == '　'; // Chinese space
    }


    private static final boolean[] firstIdentifierFlags = new boolean[256];

    static {
        // the Latin letter decimal values range between 192('À') and 255('ÿ') but exclude 215('×') and 247('÷').
        for (char c = 0; c < firstIdentifierFlags.length; ++c) {
            if ((c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= 'À' && c <= 'ÿ' && c != '×' && c != '÷')) {
                firstIdentifierFlags[c] = true;
            }
        }
        firstIdentifierFlags['`'] = true;
        firstIdentifierFlags['_'] = true;
        firstIdentifierFlags['$'] = true;
    }

    public static boolean isFirstIdentifierChar(char c) {
        if (c <= firstIdentifierFlags.length) {
            return firstIdentifierFlags[c];
        }
        return c != '　' && c != '，';
    }
}
