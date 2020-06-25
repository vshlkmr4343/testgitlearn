package com.tradiuus.widgets.maskededittext;


abstract class MaskCharacter {
    abstract public boolean isValidCharacter(char ch);

    public char processCharacter(char ch) {
        return ch;
    }

    public boolean isPrepopulate() {
        return false;
    }

    class DigitCharacter extends MaskCharacter {
        @Override
        public boolean isValidCharacter(char ch) {
            return Character.isDigit(ch);
        }

    }
}



