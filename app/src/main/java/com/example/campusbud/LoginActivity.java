package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String AUTH_KEY = BuildConfig.AUTH_KEY;
    private static final String APP_ID = BuildConfig.APP_ID;
    private static final String REGION = BuildConfig.REGION;

    private TextInputLayout mLayoutUsername;
    private TextInputLayout mLayoutPassword;
    private TextView mTvLoginError;

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

        mLayoutUsername = findViewById(R.id.layoutUsernameLogin);
        mLayoutPassword = findViewById(R.id.layoutPasswordLogin);
        mTvLoginError = findViewById(R.id.tvLoginError);
        Button mBtnLogin = findViewById(R.id.btnLogin);
        TextView mTvSignUp = findViewById(R.id.tvSignUp);

        mTvLoginError.setVisibility(View.GONE);

        mBtnLogin.setOnClickListener(v -> {
            Log.i(TAG, "onClick login button");
            String mUsername = Objects.requireNonNull(mLayoutUsername.getEditText()).getText().toString();
            String mPassword = Objects.requireNonNull(mLayoutPassword.getEditText()).getText().toString();
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
                mTvLoginError.setVisibility(View.VISIBLE);
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
        });
    }
}