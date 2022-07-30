package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class NameActivity extends AppCompatActivity {

    private TextInputLayout mEtNewName;

    private String mNewName;
    private User user;

    private static final String TAG = "NameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        user = CometChat.getLoggedInUser();
        JSONObject mMetadata = user.getMetadata();

        mEtNewName = findViewById(R.id.layoutNewName);
        Button mBtnNameContinue = findViewById(R.id.btnNameContinue);

        mBtnNameContinue.setOnClickListener(v -> {
            mNewName = Objects.requireNonNull(mEtNewName.getEditText()).getText().toString();
            try {
                mMetadata.put("name", mNewName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUser(user);

            Intent intent = new Intent(getApplicationContext(), ProfileSettings.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateUser(User user) {
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, user.toString());
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, e.getMessage());
            }
        });
    }
}