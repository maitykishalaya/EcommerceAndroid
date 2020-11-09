package in.midnaporeactacademy.doorstepmidnaporegrocery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import in.midnaporeactacademy.doorstepmidnaporegrocery.R;

public class SplashScreenActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView slogan, downLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        topAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.bottom_animation);

        image = (ImageView) findViewById(R.id.splash_image);
        slogan = (TextView) findViewById(R.id.splash_text);
        downLine = (TextView) findViewById(R.id.splash_slogan);

        image.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);
        downLine.setAnimation(bottomAnim);

        int SPLASH_SCREEN = 500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}