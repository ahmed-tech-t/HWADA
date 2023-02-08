package com.example.hwada.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProgressDrawable extends Drawable {
    private ProgressBar progressBar;

    public ProgressDrawable(Context context) {
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        progressBar.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        progressBar.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        progressBar.getIndeterminateDrawable().setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return progressBar.getLayoutParams().width;
    }

    @Override
    public int getIntrinsicHeight() {
        return progressBar.getLayoutParams().height;
    }
}
