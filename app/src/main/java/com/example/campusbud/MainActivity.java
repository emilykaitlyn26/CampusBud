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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_components.chats.CometChatConversationList;
import com.example.campusbud.fragments.ProfileFragment;
import com.example.campusbud.fragments.RoommateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("CampusBud");

        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottomNavigation);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
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
            mFragmentManager.beginTransaction().replace(R.id.flContainer, mFragment).detach(mFragment).attach(mFragment).commit();
            return true;
        });
        mBottomNavigationView.setSelectedItemId(R.id.action_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //MenuItem mLogout = findViewById(R.id.miLogout);
        //mLogout.setIcon(getDrawable(R.drawable.logout));
        if (item.getItemId() == R.id.miLogout) {
            Log.i(TAG, "Clicked logout button");
            ParseUser.logOutInBackground(e -> {
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
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}