package com.tradiuus.widgets.maskededittext;

public class DigitCharacter extends MaskCharacter {
    @Override
    public boolean isValidCharacter(char ch) {
        return Character.isDigit(ch);
    }

}