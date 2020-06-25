package com.tradiuus.widgets.maskededittext;

public class FormattedString extends AbstractFormattedString {

    public FormattedString(Mask mask, String rawString) {
        super(mask, rawString);
    }

    public String buildRawString(String str) {
        StringBuilder builder = new StringBuilder();
        int inputLen = Math.min(mMask.size(), str.length());
        for (int i = 0; i < inputLen; i++) {
            char ch = str.charAt(i);
            if (!mMask.isValidPrepopulateCharacter(ch, i))
                builder.append(ch);
        }
        return builder.toString();
    }

    public String formatString() {
        StringBuilder builder = new StringBuilder();

        int strIndex = 0;
        int maskCharIndex = 0;
        char stringCharacter;

        while (strIndex < getInputString().length() && maskCharIndex < mMask.size()) {
            MaskCharacter maskChar = mMask.get(maskCharIndex);

            stringCharacter = getInputString().charAt(strIndex);

            if (maskChar.isValidCharacter(stringCharacter)) {
                builder.append(maskChar.processCharacter(stringCharacter));
                strIndex += 1;
                maskCharIndex += 1;
            } else if (maskChar.isPrepopulate()) {
                builder.append(maskChar.processCharacter(stringCharacter));
                maskCharIndex += 1;
            } else {
                strIndex += 1;
            }
        }

        return builder.toString();
    }
}