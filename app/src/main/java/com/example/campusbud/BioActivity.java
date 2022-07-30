package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class BioActivity extends AppCompatActivity {

    private TextInputLayout mEtBio;
    private String mBio;
    private TextView mTvErrorMessageBio;
    private TextView mTvLengthError;

    private static final String TAG = "BioActivity";
    private static final int MAX_LENGTH = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        mEtBio = findViewById(R.id.bio);
        Button mBtnSubmit = findViewById(R.id.btnSubmit);
        mTvErrorMessageBio = findViewById(R.id.tvErrorMessageBio);
        mTvLengthError = findViewById(R.id.tvLengthError);
        mTvErrorMessageBio.setVisibility(View.GONE);
        mTvLengthError.setVisibility(View.GONE);

        mBtnSubmit.setOnClickListener(v -> {
            mBio = Objects.requireNonNull(mEtBio.getEditText()).getText().toString();
            if (!mBio.equals("")) {
                mTvErrorMessageBio.setVisibility(View.GONE);
                User mUser = CometChat.getLoggedInUser();
                JSONObject mMetadata = mUser.getMetadata();
                try {
                    JSONArray mRoommateProfileArray = mMetadata.getJSONArray("roommate_profile");
                    JSONObject mRoommateProfile = mRoommateProfileArray.getJSONObject(0);
                    mRoommateProfile.put("bio", mBio);
                    updateUser(mUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mTvErrorMessageBio.setVisibility(View.VISIBLE);
            }
            if (mBio.length() > MAX_LENGTH) {
                mTvLengthError.setVisibility(View.VISIBLE);
            } else {
                Intent intent = new Intent(getApplicationContext(), ProfileSettings.class);
                startActivity(intent);
                finish();
            }
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