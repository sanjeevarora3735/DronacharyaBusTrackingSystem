package com.example.dbts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStarted extends AppCompatActivity {
    private static final int OneTimeScreen = 0;
    Button GetStarted; // initializing For GetStarted Operations
    TextView Signup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        getWindow().setStatusBarColor(getResources().getColor(R.color.NonActivatedButtonColor));
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();


        GetStarted = findViewById(R.id.TrackYourChild); // Fetch Button From XML File
        GetStarted.setOnClickListener(v -> {
            // Navigation to Signup Activity
            startActivity(new Intent(GetStarted.this, Signin.class));
        });
        Signup = findViewById(R.id.Signup_Link); // Fetch Button From XML File
        Signup.setOnClickListener(v -> {
            // Navigation to Signup Activity
            startActivity(new Intent(GetStarted.this, Signup.class));
        });
    }

    @Override
    public void onStart() {
        // Check if user is signed in (non-null) and update UI accordingly.
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
//            Toast.makeText(this, "There is someone ...", Toast.LENGTH_SHORT).show();
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currFirebaseUser) {
//        Toast.makeText(this, "Update UI Function Called", Toast.LENGTH_SHORT).show();

    startActivity(new Intent(GetStarted.this, Dashboard.class));
    }
}