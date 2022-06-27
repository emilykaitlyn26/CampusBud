package com.example.campusbud;

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
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.example.campusbud.fragments.ChatFragment;
import com.example.campusbud.fragments.ProfileFragment;
import com.example.campusbud.fragments.RoommateFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public String appID = "212415705e6b5f7e";
    private String region = "us";
    public String authKey = "c523b47dfef8a387d934b40bbcf7d7bc5fe2c0ee";

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        FragmentManager fragmentManager = getSupportFragmentManager();
        
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_chat:
                        Toast.makeText(MainActivity.this, "Chat", Toast.LENGTH_SHORT).show();
                        fragment = new ChatFragment();
                        break;
                    case R.id.action_profile:
                        Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_matching:
                    default:
                        Toast.makeText(MainActivity.this, "Roommates", Toast.LENGTH_SHORT).show();
                        fragment = new RoommateFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        
        bottomNavigationView.setSelectedItemId(R.id.action_profile);

        //startActivity(new Intent(MainActivity.this, CometChatUI.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //SearchView searchView = (SearchView) menu.findItem(R.id.miLogout).getActionView();
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
                    //ParseUser currentUser = ParseUser.getCurrentUser();
                    Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    //return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /* have bottom navigation bar with profile, roommates, map, and chat
    when chat is opened, bar on top to switch to normal and groups (if i can't get them together)
    profile will have logout button there
     */

}