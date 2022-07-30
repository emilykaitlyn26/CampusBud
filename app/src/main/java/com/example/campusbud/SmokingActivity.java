package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SmokingActivity extends AppCompatActivity {

    private String mNewSmoking;

    User user;
    JSONObject mRoommateData = new JSONObject();

    private static final String TAG = "SmokingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoking);

        user = CometChat.getLoggedInUser();
        JSONObject mMetadata = user.getMetadata();
        try {
            JSONArray mRoommateArray = mMetadata.getJSONArray("roommate_profile");
            mRoommateData = mRoommateArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RadioGroup mRgNewSmoking = findViewById(R.id.rgNewSmoking);
        Button mBtnSmokingContinue = findViewById(R.id.btnSmokingContinue);

        mRgNewSmoking.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbNewSmoking = findViewById(checkedId);
            mNewSmoking = mRbNewSmoking.getText().toString();
        });

        mBtnSmokingContinue.setOnClickListener(v -> {
            try {
                mRoommateData.put("if_smoke", mNewSmoking);
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