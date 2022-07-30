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

public class CleanlinessActivity extends AppCompatActivity {

    private String mNewCleanliness;

    User user;
    JSONObject mRoommateData = new JSONObject();

    private static final String TAG = "CleanlinessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleanliness);

        user = CometChat.getLoggedInUser();
        JSONObject mMetadata = user.getMetadata();
        try {
            JSONArray mRoommateArray = mMetadata.getJSONArray("roommate_profile");
            mRoommateData = mRoommateArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RadioGroup mRgNewCleanliness = findViewById(R.id.rgNewCleanliness);
        Button mBtnCleanlinessContinue = findViewById(R.id.btnCleanlinessContinue);

        mRgNewCleanliness.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbNewCleanliness = findViewById(checkedId);
            mNewCleanliness = mRbNewCleanliness.getText().toString();
        });

        mBtnCleanlinessContinue.setOnClickListener(v -> {
            try {
                mRoommateData.put("cleanliness", mNewCleanliness);
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