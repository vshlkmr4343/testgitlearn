package com.tradiuus.widgets.maskededittext;

public class LiteralCharacter extends MaskCharacter {
    private Character character;

    LiteralCharacter() {
        character = null;
    }

    LiteralCharacter(char ch) {
        character = ch;
    }

    @Override
    public boolean isValidCharacter(char ch) {
        return character == null || character == ch;
    }


    @Override
    public char processCharacter(char ch) {
        if (character != null)
            return character;
        return ch;
    }

    public boolean isPrepopulate() {
        return character != null;
    }
}