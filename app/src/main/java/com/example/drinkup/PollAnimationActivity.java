package com.example.drinkup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class PollAnimationActivity extends AppCompatActivity {
    ImageView animation;
    private static int ANIMATION_TIME= 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_animation);

        animation = findViewById(R.id.animation_view);
        final Intent currentIntent = getIntent();
        boolean isAnswerCorrect = Boolean.parseBoolean(currentIntent.getStringExtra("isAnswerCorrect"));

        if (isAnswerCorrect) {
            animation.setImageResource(R.drawable.celebration_animation);
        } else {
            animation.setImageResource(R.drawable.beer_animation_cropped);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
            }
        }, ANIMATION_TIME);
    }

    @Override
    public void onBackPressed() {
        /*
        Override default transition effect when back button pressed
         */
        super.onBackPressed();
        overridePendingTransition(R.transition.fadein_slow, R.transition.fadeout_slow);
    }
}
