package com.tradiuus.widgets.maskededittext;

public class HexCharacter extends MaskCharacter {
    private static final String HEX_CHARS = "0123456789ABCDEF";

    @Override
    public boolean isValidCharacter(char ch) {
        return Character.isLetterOrDigit(ch) && HEX_CHARS.indexOf(Character.toUpperCase(ch)) != -1;
    }

    @Override
    public char processCharacter(char ch) {
        return Character.toUpperCase(ch);
    }
}