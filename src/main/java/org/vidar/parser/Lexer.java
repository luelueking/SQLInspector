package org.vidar.parser;

import java.util.Arrays;
import java.util.List;

import static org.vidar.parser.CharType.isFirstIdentifierChar;
import static org.vidar.parser.CharType.isWhitespace;
import static org.vidar.parser.Constant.EOI;

/**
 * 词法分析器
 */
public class Lexer {


    public final String text;

    // 当前Token
    protected Token token;

    protected String stringVal;

    protected int pos;
    protected int mark;
    protected char ch;

    protected int line;
    protected int lines;
    protected List<String> comments;

    protected char[] buf;
    protected int bufPos;

    protected int startPos;
    protected int posLine;
    protected int posColumn;

    public Lexer(String input) {
        text = input;
        pos = 0;
        ch = charAt(pos);
        while (ch == '\u200B' || ch == '\n') {
            if (ch == '\n') {
                line++;
            }
            ch = charAt(++pos);
        }
    }

    /**
     * 获取下一个token
     */
    public final void nextToken() {
        startPos = pos;
        bufPos = 0;

        // 忽略注释
        if (comments != null && !comments.isEmpty()) {
            comments = null;
        }

        this.lines = 0;

        int startLine = line;

        for (; ; ) {
            if (isWhitespace(ch)) {
                // 处理换行
                if (ch == '\n') {
                    line++;
                    lines = line - startLine;
                    ch = charAt(++pos);
                    startPos = pos;
                    continue;
                }

                // 处理文件结束或数据流的结束表示符
                if (ch == EOI && pos < text.length()) {
                    ch = charAt(++pos);
                    continue;
                }

                // 是否可以作为标识符的第一个字符
                if (isFirstIdentifierChar(ch)) {
                    if (ch == '（') {
                        scanChar();
                        token = Token.LPAREN;
                        return;
                    } else if (ch == '）') {
                        scanChar();
                        token = Token.LPAREN;
                        return;
                    }

                    // oracle NCHAR
                    if (charAt(pos + 1) == '\'') {
                        ++pos;
                        ch = '\'';
                        scanString();
                        token = Token.LITERAL_NCHARS;
                        return;
                    }

                    if (ch == '—' && charAt(pos + 1) == '—' && charAt(pos + 2) == '\n') {
                        pos += 3;
                        ch = charAt(pos);
                        continue;
                    }

                    scanIdentifier();
                    return;
                }




            }
        }
    }

    public void scanIdentifier() {
        // TODO
    }


    protected void scanString() {
        mark = pos;
        boolean hasSpecial = false;
        Token preToken = this.token;

        for (; ; ) {
            if (isEOF()) {
                lexError("unclosed.str.lit");
                return;
            }

            ch = charAt(++pos);

            if (ch == '\'') {
                scanChar();
                if (ch != '\'') {
                    token = Token.LITERAL_CHARS;
                    break;
                } else {
                    if (!hasSpecial) {
                        initBuff(bufPos);
                        arraycopy(mark + 1, buf, 0, bufPos);
                        hasSpecial = true;
                    }
                    putChar('\'');
                    continue;
                }
            }

            if (!hasSpecial) {
                bufPos++;
                continue;
            }

            if (bufPos == buf.length) {
                putChar(ch);
            } else {
                buf[bufPos++] = ch;
            }
        }

        if (!hasSpecial) {
            if (preToken == Token.AS) {
                stringVal = subString(mark, bufPos + 2);
            } else {
                stringVal = subString(mark + 1, bufPos);
            }
        } else {
            stringVal = new String(buf, 0, bufPos);
        }
    }


    public void arraycopy(int srcPos, char[] dest, int destPos, int length) {
        text.getChars(srcPos, srcPos + length, dest, destPos);
    }

    protected void initBuff(int size) {
        if (buf == null) {
            if (size < 32) {
                buf = new char[32];
            } else {
                buf = new char[size + 32];
            }
        } else if (buf.length < size) {
            buf = Arrays.copyOf(buf, size);
        }
    }

    public final char charAt(int index) {
        if (index >= text.length()) {
            return EOI;
        }

        return text.charAt(index);
    }

    protected final void scanChar() {
        ch = charAt(++pos);
    }

    public final String subString(int offset, int count) {
        return text.substring(offset, offset + count);
    }

    /**
     * Append a character to sbuf.
     */
    protected final void putChar(char ch) {
        if (bufPos == buf.length) {
            char[] newsbuf = new char[buf.length * 2];
            System.arraycopy(buf, 0, newsbuf, 0, buf.length);
            buf = newsbuf;
        }
        buf[bufPos++] = ch;
    }

    public boolean isEOF() {
        return pos >= text.length();
    }

    /**
     * Report an error at the given position using the provided arguments.
     */
    protected void lexError(String key, Object... args) {
        token = Token.ERROR;
    }


}


