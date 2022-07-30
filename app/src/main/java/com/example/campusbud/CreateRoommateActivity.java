package com.example.campusbud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class CreateRoommateActivity extends AppCompatActivity {

    private final JSONObject mMetadata = new JSONObject();
    private final JSONArray mUserActivity = new JSONArray();
    private final JSONArray mUserYearActivity = new JSONArray();
    private final JSONObject mUserYearValues = new JSONObject();
    private final JSONArray mUserCleanlinessActivity = new JSONArray();
    private final JSONObject mUserCleanlinessValues = new JSONObject();
    private final JSONArray mUserSmokingActivity = new JSONArray();
    private final JSONObject mUserSmokingValues = new JSONObject();
    private final JSONArray mUserDrinkingActivity = new JSONArray();
    private final JSONObject mUserDrinkingValues = new JSONObject();
    private final JSONArray mUserRoomUseActivity = new JSONArray();
    private final JSONObject mUserRoomUseValues = new JSONObject();
    private final JSONArray mUserRates = new JSONArray();
    private final JSONObject mUserRateValues = new JSONObject();
    private final JSONArray mRoommateProfileArray = new JSONArray();
    private final JSONObject mRoommateProfile = new JSONObject();

    private String mUsername;
    private String mPassword;
    private String mCollege;
    private String mYear;
    private String mName;
    private String mMajor;
    private String mGender;
    private String mInterest1;
    private String mInterest2;
    private String mInterest3;
    private String mActivity1;
    private String mActivity2;
    private String mActivity3;
    private String mCleanliness;
    private String mDrinking;
    private String mSmoking;
    private String mRoomUse;
    private String mTimeWake;
    private String mTimeSleep;
    private String mBio;
    private Boolean mSwitchState = false;

    private final String[] mTimes = {"12:00am", "1:00am", "2:00am", "3:00am", "4:00am", "5:00am", "6:00am", "7:00am", "8:00am", "9:00am", "10:00am", "11:00am", "12:00pm", "1:00pm", "2:00pm", "3:00pm", "4:00pm", "5.00pm", "6:00pm", "7.00pm", "8:00pm", "9:00pm", "10:00pm", "11:00pm"};

    private static final String TAG = "CreateRoommateActivity";
    private static final String AUTH_KEY = BuildConfig.AUTH_KEY;
    private static final int MAX_BIO_LENGTH = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_roommate);

        mUsername = getIntent().getStringExtra("username");
        mPassword = getIntent().getStringExtra("password");
        mCollege = getIntent().getStringExtra("college");
        mYear = getIntent().getStringExtra("year");
        mMajor = getIntent().getStringExtra("major");
        mName = getIntent().getStringExtra("name");
        mGender = getIntent().getStringExtra("gender");
        mInterest1 = getIntent().getStringExtra("interest1");
        mInterest2 = getIntent().getStringExtra("interest2");
        mInterest3 = getIntent().getStringExtra("interest3");
        mActivity1 = getIntent().getStringExtra("activity1");
        mActivity2 = getIntent().getStringExtra("activity2");
        mActivity3 = getIntent().getStringExtra("activity3");

        AutoCompleteTextView mSleepTextView = findViewById(R.id.etTimeSleep);
        AutoCompleteTextView mWakeTextView = findViewById(R.id.etTimeWake);
        ArrayAdapter<String> mSleeptimeadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mTimes);
        ArrayAdapter<String> mWaketimeadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mTimes);
        mSleepTextView.setThreshold(1);
        mSleepTextView.setAdapter(mSleeptimeadapter);
        mWakeTextView.setThreshold(1);
        mWakeTextView.setAdapter(mWaketimeadapter);

        RadioGroup mRgCleanliness = findViewById(R.id.rgCleanliness);
        RadioGroup mRgDrinking = findViewById(R.id.rgDrinking);
        RadioGroup mRgSmoking = findViewById(R.id.rgSmoking);
        RadioGroup mRgRoomUse = findViewById(R.id.rgRoomUse);
        Button mBtnCreateAccount = findViewById(R.id.btnCreateImagesContinue);
        TextInputLayout mLayoutBio = findViewById(R.id.layoutBio);
        TextView mTvErrorBio = findViewById(R.id.tvBioError);
        TextView mTvErrorAll = findViewById(R.id.tvErrorAll2);

        mTvErrorAll.setVisibility(View.GONE);
        mTvErrorBio.setVisibility(View.GONE);

        //EditText mEtBio = findViewById(R.id.etBio);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch mSwitchProfile = findViewById(R.id.switchProfile);

        mSwitchProfile.setOnCheckedChangeListener((buttonView, isChecked) -> mSwitchState = mSwitchProfile.isChecked());

        mRgCleanliness.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbCleanliness = findViewById(checkedId);
            mCleanliness = mRbCleanliness.getText().toString();
        });

        mRgDrinking.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbDrinking = findViewById(checkedId);
            mDrinking = mRbDrinking.getText().toString();
        });

        mRgSmoking.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbSmoking = findViewById(checkedId);
            mSmoking = mRbSmoking.getText().toString();
        });

        mRgRoomUse.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbRoomUse = findViewById(checkedId);
            mRoomUse = mRbRoomUse.getText().toString();
        });

        mWakeTextView.setOnItemClickListener((parent, view, position, id) -> mTimeWake = (String) parent.getItemAtPosition(position));

        mSleepTextView.setOnItemClickListener((parent, view, position, id) -> mTimeSleep = (String) parent.getItemAtPosition(position));

        mBtnCreateAccount.setOnClickListener(v -> {
            try {
                if  (Objects.requireNonNull(mLayoutBio.getEditText()).getText().toString().length() > MAX_BIO_LENGTH) {
                    mTvErrorBio.setVisibility(View.VISIBLE);
                } else if (!(mLayoutBio.getEditText().getText().toString().trim()).equals("") && mCleanliness != null && mDrinking != null && mSmoking != null && mRoomUse != null && mTimeSleep != null && mTimeWake != null) {
                    mBio = mLayoutBio.getEditText().getText().toString();
                    signUpUser(mUsername, mPassword);
                } else {
                    mTvErrorAll.setVisibility(View.VISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void signUpUser(String username, String password) throws IOException {
        Log.i(TAG, "Attempting to sign up user" + username);
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }
        ParseUser mParseUser = new ParseUser();
        mParseUser.setUsername(username);
        mParseUser.setPassword(password);
        mParseUser.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with sign up", e);
                return;
            }
            String mUID = mParseUser.getObjectId();
            User mUser = new User();
            mUser.setUid(mUID);
            mUser.setName(username);

            CometChat.createUser(mUser, AUTH_KEY, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d("createUser", user.toString());
                    loginUser(username, password);
                }
                @Override
                public void onError(CometChatException e) {
                    Log.e("createUser", e.getMessage());
                }
            });
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with login", e);
                return;
            }
            ParseUser mParseUser = ParseUser.getCurrentUser();
            String mUID = mParseUser.getObjectId();

            if (CometChat.getLoggedInUser() == null) {
                CometChat.login(mUID, AUTH_KEY, new CometChat.CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d(TAG, "Login Successful : " + user.toString());
                        try {
                            setProfile();
                            setUserActivity();
                            goImages();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.d(TAG, "Login failed with exception: " + e.getMessage());
                    }
                });
            }
        });
    }

    public void setUserActivity() throws JSONException {
        User newUser = CometChat.getLoggedInUser();
        mMetadata.put("num_refreshed", 0);
        mUserYearValues.put("freshman", 0);
        mUserYearValues.put("sophomore", 0);
        mUserYearValues.put("junior", 0);
        mUserYearValues.put("senior", 0);
        mUserYearActivity.put(mUserYearValues);
        mUserCleanlinessValues.put("organized", 0);
        mUserCleanlinessValues.put("casual", 0);
        mUserCleanlinessValues.put("occasionally_messy", 0);
        mUserCleanlinessValues.put("messy", 0);
        mUserCleanlinessActivity.put(mUserCleanlinessValues);
        mUserSmokingValues.put("yes", 0);
        mUserSmokingValues.put("sometimes", 0);
        mUserSmokingValues.put("no", 0);
        mUserSmokingActivity.put(mUserSmokingValues);
        mUserDrinkingValues.put("yes", 0);
        mUserDrinkingValues.put("sometimes", 0);
        mUserDrinkingValues.put("no", 0);
        mUserDrinkingActivity.put(mUserDrinkingValues);
        mUserRoomUseValues.put("social_space", 0);
        mUserRoomUseValues.put("study_space", 0);
        mUserRoomUseValues.put("sleeping_space", 0);
        mUserRoomUseValues.put("all_above", 0);
        mUserRoomUseActivity.put(mUserRoomUseValues);
        mUserActivity.put(mUserYearActivity);
        mUserActivity.put(mUserCleanlinessActivity);
        mUserActivity.put(mUserSmokingActivity);
        mUserActivity.put(mUserDrinkingActivity);
        mUserActivity.put(mUserRoomUseActivity);
        mMetadata.put("user_activity", mUserActivity);

        mUserRateValues.put("year_max", 0.7);
        mUserRateValues.put("year_average", 0.5);
        mUserRateValues.put("year_below", 0.3);
        mUserRateValues.put("year_lowest", 0.1);
        mUserRateValues.put("clean_max", 0.7);
        mUserRateValues.put("clean_average", 0.5);
        mUserRateValues.put("clean_below", 0.3);
        mUserRateValues.put("clean_lowest", 0.1);
        mUserRateValues.put("smoke_max", 0.7);
        mUserRateValues.put("smoke_average", 0.5);
        mUserRateValues.put("smoke_below", 0.3);
        mUserRateValues.put("drink_max", 0.7);
        mUserRateValues.put("drink_average", 0.5);
        mUserRateValues.put("drink_below", 0.3);
        mUserRateValues.put("room_max", 0.7);
        mUserRateValues.put("room_average", 0.5);
        mUserRateValues.put("room_below", 0.3);
        mUserRateValues.put("room_lowest", 0.1);
        mUserRates.put(mUserRateValues);
        mMetadata.put("rate_values", mUserRates);
        newUser.setMetadata(mMetadata);
        updateUser(newUser);
    }

    private void setProfile() throws JSONException {
        User newUser = CometChat.getLoggedInUser();
        mMetadata.put("name", mName);
        mMetadata.put("college", mCollege);
        mMetadata.put("major", mMajor);
        mMetadata.put("year", mYear);
        mMetadata.put("gender", mGender);
        mMetadata.put("ifSwitched", mSwitchState);
        mRoommateProfile.put("time_sleep", mTimeSleep);
        mRoommateProfile.put("time_wake", mTimeWake);
        mRoommateProfile.put("cleanliness", mCleanliness);
        mRoommateProfile.put("if_smoke", mSmoking);
        mRoommateProfile.put("if_drink", mDrinking);
        mRoommateProfile.put("room_use", mRoomUse);
        mRoommateProfile.put("interest1", mInterest1);
        mRoommateProfile.put("interest2", mInterest2);
        mRoommateProfile.put("interest3", mInterest3);
        mRoommateProfile.put("activity1", mActivity1);
        mRoommateProfile.put("activity2", mActivity2);
        mRoommateProfile.put("activity3", mActivity3);
        mRoommateProfile.put("bio", mBio);
        mRoommateProfileArray.put(mRoommateProfile);
        mMetadata.put("roommate_profile", mRoommateProfileArray);
        newUser.setMetadata(mMetadata);
        updateUser(newUser);
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

    private void goImages() {
        Intent intent = new Intent(this, CreateImagesActivity.class);
        startActivity(intent);
        finish();
    }

}