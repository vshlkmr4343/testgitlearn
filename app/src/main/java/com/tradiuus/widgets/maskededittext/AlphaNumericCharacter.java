package com.tradiuus.widgets.maskededittext;

public class AlphaNumericCharacter extends MaskCharacter {
    @Override
    public boolean isValidCharacter(char ch) {
        return Character.isLetterOrDigit(ch);
    }
}