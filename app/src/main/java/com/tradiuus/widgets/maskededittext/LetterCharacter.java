package com.tradiuus.widgets.maskededittext;

public class LetterCharacter extends MaskCharacter {
    @Override
    public boolean isValidCharacter(char ch) {
        return Character.isLetter(ch);
    }
}