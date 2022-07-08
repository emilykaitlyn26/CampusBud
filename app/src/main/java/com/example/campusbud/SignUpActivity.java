package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {

    public final String TAG = "SignUpActivity";
    public static final String authKey = "c523b47dfef8a387d934b40bbcf7d7bc5fe2c0ee";

    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick sign up button");
                String username = etNewUsername.getText().toString();
                String password = etNewPassword.getText().toString();
                if (!username.trim().equals("") && !password.trim().equals("")) {
                    try {
                        signUpUser(username, password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Username/ password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUpUser(String username, String password) throws IOException {
        Log.i(TAG, "Attempting to sign up user" + username);
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with sign up", e);
                Toast.makeText(this, "Issue with sign up", Toast.LENGTH_SHORT).show();
                return;
            }
            String UID = parseUser.getObjectId();
            User user = new User();
            user.setUid(UID);
            user.setName(username);

            CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d("createUser", user.toString());
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e("createUser", e.getMessage());
                }
            });

            loginUser(username, password);
            Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(SignUpActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser parseUser = ParseUser.getCurrentUser();
                String UID = parseUser.getObjectId();

                if (CometChat.getLoggedInUser() == null) {
                    CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {
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
                Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}