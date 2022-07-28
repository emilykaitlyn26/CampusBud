package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BioActivity extends AppCompatActivity {

    private EditText mEtBio;
    private String mBio;
    private TextView mTvErrorMessageBio;
    private TextView mTvLengthError;

    private static final String TAG = "BioActivity";
    private static final int MAX_LENGTH = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        mEtBio = findViewById(R.id.etBio);
        Button mBtnSubmit = findViewById(R.id.btnSubmit);
        mTvErrorMessageBio = findViewById(R.id.tvErrorMessageBio);
        mTvLengthError = findViewById(R.id.tvLengthError);
        mTvErrorMessageBio.setVisibility(View.GONE);
        mTvLengthError.setVisibility(View.GONE);

        mBtnSubmit.setOnClickListener(v -> {
            mBio = mEtBio.getText().toString();
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
            }
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