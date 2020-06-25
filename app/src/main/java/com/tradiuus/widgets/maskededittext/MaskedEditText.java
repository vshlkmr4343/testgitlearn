package com.tradiuus.widgets.maskededittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.tradiuus.R;
import com.tradiuus.widgets.CustomEditText;

public class MaskedEditText extends CustomEditText {

    // ===========================================================
    // Fields
    // ===========================================================

    private MaskedFormatter mMaskedFormatter;
    private MaskedWatcher mMaskedWatcher;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MaskedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaskedEditText);

        if (typedArray.hasValue(R.styleable.MaskedEditText_mask)) {
            String maskStr = typedArray.getString(R.styleable.MaskedEditText_mask);

            if (maskStr != null && !maskStr.isEmpty()) {
                setMask(maskStr);
            }
        }

        typedArray.recycle();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public String getMaskString() {
        return mMaskedFormatter.getMaskString();
    }

    public String getUnMaskedText() {
        String currentText = getText().toString();
        IFormattedString formattedString = mMaskedFormatter.formatString(currentText);
        return formattedString.getUnMaskedString();
    }

    public void setMask(String mMaskStr) {
        mMaskedFormatter = new MaskedFormatter(mMaskStr);

        if (mMaskedWatcher != null) {
            removeTextChangedListener(mMaskedWatcher);
        }

        mMaskedWatcher = new MaskedWatcher(mMaskedFormatter, this);
        addTextChangedListener(mMaskedWatcher);
    }

}
