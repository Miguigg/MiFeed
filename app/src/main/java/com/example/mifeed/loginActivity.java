package com.example.mifeed;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void onBackPressed()
    {
        Intent intent = new Intent(loginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}