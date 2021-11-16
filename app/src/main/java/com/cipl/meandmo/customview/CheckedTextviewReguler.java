package com.cipl.meandmo.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckedTextView;

/**
 * Created by Dhruv rathod on 8/12/2021
 */
public class CheckedTextviewReguler extends AppCompatCheckedTextView {
    public CheckedTextviewReguler(@NonNull Context context) {
        super(context);
        init();
    }

    public CheckedTextviewReguler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CheckedTextviewReguler(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/RobotoCondensed-Regular.ttf");
        setTypeface(tf, 1);

    }


}
