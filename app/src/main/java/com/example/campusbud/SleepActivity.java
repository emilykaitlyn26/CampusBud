package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SleepActivity extends AppCompatActivity {

    private final String[] mTimes = {"12:00am", "1:00am", "2:00am", "3:00am", "4:00am", "5:00am", "6:00am", "7:00am", "8:00am", "9:00am", "10:00am", "11:00am", "12:00pm", "1:00pm", "2:00pm", "3:00pm", "4:00pm", "5.00pm", "6:00pm", "7.00pm", "8:00pm", "9:00pm", "10:00pm", "11:00pm"};

    private String mTimeSleep;
    private User currentUser;

    private static final String TAG = "SleepActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        currentUser = CometChat.getLoggedInUser();
        JSONObject mMetadata = currentUser.getMetadata();
        JSONObject mRoommateData = new JSONObject();
        try {
            JSONArray mRoommateArray = mMetadata.getJSONArray("roommate_profile");
            mRoommateData = mRoommateArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Button mBtnWakeContinue = findViewById(R.id.btnSleepContinue);

        AutoCompleteTextView mSleepTextView = findViewById(R.id.etNewSleep);
        ArrayAdapter<String> mSleeptimeadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mTimes);
        mSleepTextView.setThreshold(1);
        mSleepTextView.setAdapter(mSleeptimeadapter);
        mSleepTextView.setOnItemClickListener((parent, view, position, id) -> mTimeSleep = (String) parent.getItemAtPosition(position));

        JSONObject finalMRoommateData = mRoommateData;
        mBtnWakeContinue.setOnClickListener(v -> {
            try {
                finalMRoommateData.put("time_sleep", mTimeSleep);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUser(currentUser);

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