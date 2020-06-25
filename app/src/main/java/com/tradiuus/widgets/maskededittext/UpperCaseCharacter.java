package com.tradiuus.widgets.maskededittext;

public class UpperCaseCharacter extends MaskCharacter {
    @Override
    public boolean isValidCharacter(char ch) {
        return Character.isUpperCase(ch);
    }

    @Override
    public char processCharacter(char ch) {
        return Character.toUpperCase(ch);
    }
}