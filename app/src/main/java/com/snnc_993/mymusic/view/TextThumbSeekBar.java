package com.snnc_993.mymusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.snnc_993.mymusic.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TextThumbSeekBar extends AppCompatSeekBar {

    private final int mThumbSize;
    private final TextPaint mTextPaint;

    private static SimpleDateFormat dateFormat;

    public TextThumbSeekBar(Context context) {
        this(context, null);
    }

    public TextThumbSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public TextThumbSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dateFormat = new SimpleDateFormat("m:ss", new Locale("en"));
        mThumbSize = getResources().getDimensionPixelSize(R.dimen.custom_seekbar_thumb_width);
        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.custom_seekbar_thumb_text_size));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int max = this.getMax();
        String progressText = "••• / •••";
        if(max != 100){
            progressText = dateFormat.format(getProgress()) + " / " + dateFormat.format(getMax());
        }
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
        float thumbY = getHeight() / 2f + bounds.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
    }
}