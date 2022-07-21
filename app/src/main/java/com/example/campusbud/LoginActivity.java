package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String AUTH_KEY = BuildConfig.AUTH_KEY;
    private static final String APP_ID = BuildConfig.APP_ID;
    private static final String REGION = BuildConfig.REGION;

    private EditText mEtUsername;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppSettings appSettings = new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(REGION).autoEstablishSocketConnection(true).build();

        CometChat.init(this, APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                UIKitSettings.setAuthKey(AUTH_KEY);
                CometChat.setSource("ui-kit", "android", "java");
                Log.d(TAG, "Initialization completed successfully");
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });

        if (CometChat.getLoggedInUser() != null) {
            goMainActivity();
        }

        mEtUsername = findViewById(R.id.etUsernameSettings);
        mEtPassword = findViewById(R.id.etPassword);
        Button mBtnLogin = findViewById(R.id.btnLogin);
        TextView mTvSignUp = findViewById(R.id.tvSignUp);

        mBtnLogin.setOnClickListener(v -> {
            Log.i(TAG, "onClick login button");
            String mUsername = mEtUsername.getText().toString();
            String mPassword = mEtPassword.getText().toString();
            loginUser(mUsername, mPassword);
        });

        mTvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with login", e);
                Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseUser mParseUser = ParseUser.getCurrentUser();
            String mUID = mParseUser.getObjectId();

            if (CometChat.getLoggedInUser() == null) {
                CometChat.login(mUID, AUTH_KEY, new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "Login Successful : " + user.toString());
                        goMainActivity();
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.d(TAG, "Login failed with exception: " + e.getMessage());
                    }
                });
            }
            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
        });
    }
}