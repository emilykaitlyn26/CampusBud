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

import org.json.JSONException;
import org.json.JSONObject;

public class YearActivity extends AppCompatActivity {

    private String mNewYear;

    User user;

    private static final String TAG = "YearActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        user = CometChat.getLoggedInUser();
        JSONObject mMetadata = user.getMetadata();

        RadioGroup mRgNewYear = findViewById(R.id.rgNewDrinking);
        Button mBtnYearContinue = findViewById(R.id.btnDrinkingContinue);

        mRgNewYear.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbNewYear = findViewById(checkedId);
            mNewYear = mRbNewYear.getText().toString();
        });

        mBtnYearContinue.setOnClickListener(v -> {
            try {
                mMetadata.put("year", mNewYear);
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