package com.example.campusbud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.chats.CometChatConversationList;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.example.campusbud.fragments.ChatFragment;
import com.example.campusbud.fragments.ProfileFragment;
import com.example.campusbud.fragments.RoommateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottomNavigation);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment mFragment;
                switch (item.getItemId()) {
                    case R.id.action_chat:
                        Toast.makeText(MainActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        mFragment = new CometChatConversationList();
                        break;
                    case R.id.action_profile:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        mFragment = new ProfileFragment();
                        break;
                    case R.id.action_matching:
                    default:
                        Toast.makeText(MainActivity.this, "Roommates", Toast.LENGTH_SHORT).show();
                        mFragment = new RoommateFragment();
                        break;
                }
                mFragmentManager.beginTransaction().replace(R.id.flContainer, mFragment).commit();
                return true;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miLogout) {
            Log.i(TAG, "Clicked logout button");
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    CometChat.logout(new CometChat.CallbackListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, "Logout completed successfully");
                            goLoginScreen();
                        }
                        @Override
                        public void onError(CometChatException e) {
                            Log.d(TAG, "Logout failed with exception: " + e.getMessage());
                        }
                    });
                    Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}