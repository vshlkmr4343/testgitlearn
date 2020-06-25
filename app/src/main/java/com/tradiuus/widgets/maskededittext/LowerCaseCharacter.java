package com.tradiuus.widgets.maskededittext;

public class LowerCaseCharacter extends MaskCharacter {
    @Override
    public boolean isValidCharacter(char ch) {
        return Character.isLowerCase(ch);
    }

    @Override
    public char processCharacter(char ch) {
        return Character.toLowerCase(ch);
    }
}