package com.asmodeusstudio.parade;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.asmodeusstudio.parade.login.Login;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SCREEN_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent myIntent = new Intent(SplashScreen.this, Main.class);
                    startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(SplashScreen.this, Login.class);
                    startActivity(myIntent);
                }
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
