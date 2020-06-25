package com.tradiuus.widgets.maskededittext;

public abstract class AbstractFormattedString implements IFormattedString {
    private String mFormattedString;
    private final String mRawString;
    private final String mUnmaskedString;
    final Mask mMask;

    AbstractFormattedString(Mask mask, String rawString) {
        mMask = mask;
        mRawString = rawString;
        mUnmaskedString = buildRawString(rawString);
    }


    abstract String formatString();

    abstract String buildRawString(String str);

    public String getUnMaskedString() {
        return mUnmaskedString;
    }


    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public String toString() {
        if (mFormattedString == null) {
            mFormattedString = formatString();
        }
        return mFormattedString;
    }

    public String getInputString() {
        return mRawString;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

}