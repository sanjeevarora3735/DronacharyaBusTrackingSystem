package com.example.dbts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        startActivity(new Intent(SplashScreen.this,RouteSelection.class));
        if (i == 0) {
            new Handler().postDelayed(() -> {
                Intent mainIntent = new Intent(SplashScreen.this, GetStarted.class);
                startActivity(mainIntent);
                i++;
                finish();
            }, 4000);
        } else {
            new Handler().postDelayed(() -> {
                Intent mainIntent = new Intent(SplashScreen.this, GetStarted.class);
                startActivity(mainIntent);
                i++;
                finish();
            }, 2000);
        }
    }
}