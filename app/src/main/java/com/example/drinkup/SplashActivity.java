package com.example.drinkup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class SplashActivity extends AppCompatActivity {
    View background;
    View appName;
    private static int ANIMATION_CIRCLE_TIME=750;
    private static int ANIMATION_TRANSLATION_TIME=500;
    private static int SPLASH_TIME=ANIMATION_CIRCLE_TIME + ANIMATION_TRANSLATION_TIME + 400;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.transition.circlegrow, R.transition.circlegrow);
        setContentView(R.layout.activity_splash);

        background=findViewById(R.id.background_splashscreen);
        appName=findViewById(R.id.app_name_textview);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainIntent=new Intent(SplashActivity.this, EventsActivity.class);
                startActivity(MainIntent);
                finish();
            }
        }, SPLASH_TIME);

        if (savedInstanceState == null) {
            background.setVisibility(View.INVISIBLE);

            final ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        translationAnimation();
                        circularRevealActivity();
                        background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }
    }

    private void translationAnimation() {
        Animation animation = new TranslateAnimation(-1000, 0,0, 0);
        animation.setStartOffset(ANIMATION_CIRCLE_TIME);
        animation.setDuration(ANIMATION_TRANSLATION_TIME);
        animation.setFillAfter(true);
        appName.startAnimation(animation);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity() {
        int cx = background.getRight() - getDips(44);
        int cy = background.getBottom() - getDips(44);

        float finalRadius = Math.max(background.getWidth(), background.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                background,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(ANIMATION_CIRCLE_TIME);
        background.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }
}
