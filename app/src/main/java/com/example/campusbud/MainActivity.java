package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;

public class MainActivity extends AppCompatActivity {

    public String appID = "212415705e6b5f7e";
    private String region = "us";
    public String authKey = "c523b47dfef8a387d934b40bbcf7d7bc5fe2c0ee";

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, CometChatUI.class));
    }

}