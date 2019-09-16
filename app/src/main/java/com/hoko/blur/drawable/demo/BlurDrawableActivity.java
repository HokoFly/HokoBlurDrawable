package com.hoko.blur.drawable.demo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.hoko.blur.drawable.BlurDrawable;
import com.hoko.blur.view.BlurFrameLayout;


public class BlurDrawableActivity extends Activity {

    private ValueAnimator mAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blur_drawable);

        final BlurFrameLayout frameLayout = findViewById(R.id.blur_frameLayout);

        final BlurDrawable blurDrawable = new BlurDrawable();
        blurDrawable.mixColor(Color.argb(99, 255, 255, 255));
        findViewById(R.id.test_view).setBackground(blurDrawable);

        mAnimator = ValueAnimator.ofInt(0, 20);
        mAnimator.setDuration(2000);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                frameLayout.getBlurDrawable().radius((Integer) animation.getAnimatedValue());
            }
        });
    }

    public void remove(View view) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        lp.height = lp.height - 10;
        lp.width = lp.height - 20;
        view.setLayoutParams(lp);
//        ((ViewGroup) view.getParent()).removeView(view);
    }

    public void animate(View view) {
        mAnimator.start();
    }
}
