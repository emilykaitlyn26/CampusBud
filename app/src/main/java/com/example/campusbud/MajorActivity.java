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

public class MajorActivity extends AppCompatActivity {

    private TextInputLayout mEtNewMajor;

    private String mNewMajor;
    private User user;

    private static final String TAG = "NameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_major);

        user = CometChat.getLoggedInUser();
        JSONObject mMetadata = user.getMetadata();

        mEtNewMajor = findViewById(R.id.layoutNewMajor);
        Button mBtnMajorContinue = findViewById(R.id.btnMajorContinue);

        mBtnMajorContinue.setOnClickListener(v -> {
            mNewMajor = Objects.requireNonNull(mEtNewMajor.getEditText()).getText().toString();
            try {
                mMetadata.put("major", mNewMajor);
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