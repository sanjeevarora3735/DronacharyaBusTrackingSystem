package com.example.dbts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//                    FirebaseAuth.getInstance().signInWithEmailAndPassword("sanjeevarora3735@gmail.com","Lenovo00!");
//                    startActivity(new Intent(SplashScreen.this,Dashboard.class));


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