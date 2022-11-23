package com.example.dbts;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class Maintenance extends AppCompatActivity {
    private Button CloseButton ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        CloseButton = findViewById(R.id.CloseButton);
        CloseButton.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }
}