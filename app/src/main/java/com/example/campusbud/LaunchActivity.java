package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class LaunchActivity extends AppCompatActivity {

    public final String TAG = "LaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Thread splashScreenStarter =  new Thread(() ->{
            try {
                int delay = 0;
                while (delay < 1000) {
                    Thread.sleep(150);
                    delay = delay + 100;
                }
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                finish();
            }
        });
        splashScreenStarter.start();
        Log.d(TAG, "Opened launch screen");
    }
}