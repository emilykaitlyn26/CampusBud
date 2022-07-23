package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.fragments.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BioActivity extends AppCompatActivity {

    private EditText mEtBio;
    private String mBio;
    private TextView mTvErrorMessageBio;

    private static final String TAG = "BioActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        mEtBio = findViewById(R.id.etBio);
        Button mBtnSubmit = findViewById(R.id.btnSubmit);
        mTvErrorMessageBio = findViewById(R.id.tvErrorMessageBio);
        mTvErrorMessageBio.setVisibility(View.GONE);

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