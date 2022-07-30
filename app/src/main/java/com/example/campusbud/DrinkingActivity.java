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

public class DrinkingActivity extends AppCompatActivity {

    private String mNewDrinking;

    User user;
    JSONObject mRoommateData = new JSONObject();

    private static final String TAG = "DrinkingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinking);

        user = CometChat.getLoggedInUser();
        JSONObject mMetadata = user.getMetadata();
        try {
            JSONArray mRoommateArray = mMetadata.getJSONArray("roommate_profile");
            mRoommateData = mRoommateArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RadioGroup mRgNewDrinking = findViewById(R.id.rgNewDrinking);
        Button mBtnDrinkingContinue = findViewById(R.id.btnDrinkingContinue);

        mRgNewDrinking.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbNewDrinking = findViewById(checkedId);
            mNewDrinking = mRbNewDrinking.getText().toString();
        });

        mBtnDrinkingContinue.setOnClickListener(v -> {
            try {
                mRoommateData.put("if_drink", mNewDrinking);
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