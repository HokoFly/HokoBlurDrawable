package com.hoko.blur.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.ColorInt;

import com.hoko.blur.anno.Mode;
import com.hoko.blur.opengl.functor.DrawFunctor;
import com.hoko.blur.opengl.renderer.ScreenBlurRenderer;
import com.hoko.blur.util.Preconditions;

/**
 * Created by yuxfzju on 16/11/23.
 */
public class BlurDrawable extends Drawable {
    public static final int MODE_BOX = 0;
    public static final int MODE_GAUSSIAN = 1;
    public static final int MODE_STACK = 2;

    private DrawFunctor mDrawFunctor;

    private ScreenBlurRenderer mBlurRenderer;

    private int alpha;

    private Paint mPaint;
    private volatile boolean mBlurEnabled = true;

    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public BlurDrawable() {
        mBlurRenderer = new ScreenBlurRenderer.Builder().build();
        mDrawFunctor = new DrawFunctor(new DrawFunctor.DrawLocationObserver() {
            @Override
            public void onLocated(DrawFunctor.GLInfo info, boolean isChild) {
                mBlurRenderer.onDrawFrame(info, isChild);
            }

            @Override
            public void onLocateError(int what) {
                mBlurEnabled = false;
                invalidateOnMainThread();
            }
        });
        mPaint = new Paint();
        mPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas.isHardwareAccelerated() && mBlurEnabled) {
            boolean isSuccess = mDrawFunctor.doDraw(canvas);
            if (!isSuccess) {
                canvas.drawRect(getBounds(), mPaint);
            }
        } else {
            canvas.drawRect(getBounds(), mPaint);
        }
    }

    @Override
    @Deprecated
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateOnMainThread();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // setting ColorFilter is invalid
    }

    @Override
    public int getOpacity() {
        return alpha == 255 ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT;
    }


    public void disableBlur() {
        mBlurEnabled = false;
    }

    public void enableBlur() {
        mBlurEnabled = true;
    }

    private void invalidateOnMainThread() {
        Looper currentLooper = Looper.myLooper();
        if (currentLooper == null || !currentLooper.equals(Looper.getMainLooper())) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    invalidateSelf();
                }
            });
        } else {
            invalidateSelf();
        }
    }

    public void mode(@Mode int mode) {
        mBlurRenderer.mode(mode);
        invalidateOnMainThread();
    }

    public void radius(int radius) {
        mBlurRenderer.radius(radius);
        invalidateOnMainThread();
    }

    public void sampleFactor(float factor) {
        mBlurRenderer.sampleFactor(factor);
        invalidateOnMainThread();
    }

    public void mixColor(@ColorInt int mixColor) {
        mBlurRenderer.mixColor(mixColor);
        invalidateOnMainThread();

    }

    public void mixPercent(float mixPercent) {
        Preconditions.checkArgument(mixPercent <= 1.0f && mixPercent >= 0, "set 0 <= mixPercent <= 1.0f");
        mBlurRenderer.mixPercent(mixPercent);
        invalidateOnMainThread();
    }

    public void onlyDirtyRegion(boolean onlyDirtyRegion) {
        mDrawFunctor.onlyDirtyRegion(onlyDirtyRegion);
        invalidateOnMainThread();
    }

    public int mode() {
        return mBlurRenderer.mode();
    }

    public int radius() {
        return mBlurRenderer.radius();
    }

    public float sampleFactor() {
        return mBlurRenderer.sampleFactor();
    }


    @ColorInt
    public int mixColor() {
        return mBlurRenderer.mixColor();
    }

    public float mixPercent() {
        return mBlurRenderer.mixPercent();
    }

    public void freeGLResource() {
        mBlurRenderer.free();
    }

}
