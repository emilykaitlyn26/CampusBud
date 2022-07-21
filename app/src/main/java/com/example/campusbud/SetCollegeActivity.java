package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetCollegeActivity extends AppCompatActivity {

    private ArrayAdapter<String> mUniadapter;
    private List<ParseObject> mListColleges;
    private List<String> mUniversityNames;
    private ParseObject mStateObject;
    private String mSelectedCollege;
    private JSONObject mMetadata = new JSONObject();
    private JSONArray mUserActivity = new JSONArray();
    private JSONArray mUserYearActivity = new JSONArray();
    private JSONObject mUserYearValues = new JSONObject();
    private JSONArray mUserCleanlinessActivity = new JSONArray();
    private JSONObject mUserCleanlinessValues = new JSONObject();
    private JSONArray mUserSmokingActivity = new JSONArray();
    private JSONObject mUserSmokingValues = new JSONObject();
    private JSONArray mUserDrinkingActivity = new JSONArray();
    private JSONObject mUserDrinkingValues = new JSONObject();
    private JSONArray mUserRoomUseActivity = new JSONArray();
    private JSONObject mUserRoomUseValues = new JSONObject();
    //private JSONArray userSleepTimeActivity = new JSONArray();
    //private JSONArray userWakeTimeActivity = new JSONArray();
    private JSONArray mUserRates = new JSONArray();
    private JSONObject mUserRateValues = new JSONObject();

    private static final String TAG = "SetCollegeActivity";
    private static final String AUTH_KEY = BuildConfig.AUTH_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_college);

        String mUsername = getIntent().getStringExtra("username");
        String mPassword = getIntent().getStringExtra("password");
        String mState = getIntent().getStringExtra("state");
        ArrayList<State> mList = (ArrayList<State>) getIntent().getSerializableExtra("list");

        mListColleges = new ArrayList<>();
        mUniversityNames = new ArrayList<>();

        if (mList.size() == 56) {
            for (int i = 0; i < mList.size(); i++) {
                mStateObject = mList.get(i);
                String mStateName = mStateObject.getString("name");
                if (mStateName.equals(mState)) {
                    String mStateID = mStateObject.getObjectId();
                    break;
                }
            }

            ParseRelation<ParseObject> mRelation = mStateObject.getRelation("Universities");
            ParseQuery<ParseObject> query = mRelation.getQuery();
            query.findInBackground((objects, e) -> {
                if (e != null) {
                    Log.e(TAG, "Issue getting colleges");
                    return;
                }
                mListColleges.addAll(objects);
                for (int i = 0; i < mListColleges.size(); i++) {
                    ParseObject mCollegeObject = mListColleges.get(i);
                    String mName = mCollegeObject.getString("name");
                    mUniversityNames.add(mName);
                }
                mUniadapter.notifyDataSetChanged();
            });

            mUniadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mUniversityNames);
            AutoCompleteTextView mUniTextView = (AutoCompleteTextView) findViewById(R.id.chooseCollege);
            mUniTextView.setThreshold(3);
            mUniTextView.setAdapter(mUniadapter);
            mUniTextView.setOnItemClickListener((parent, view, position, id) -> mSelectedCollege = (String) parent.getItemAtPosition(position));
        }

        Button mBtnSignUp = findViewById(R.id.btnSignUp);
        mBtnSignUp.setOnClickListener(v -> {
            try {
                signUpUser(mUsername, mPassword);
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
                Toast.makeText(this, "Issue with sign up", Toast.LENGTH_SHORT).show();
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
                }
                @Override
                public void onError(CometChatException e) {
                    Log.e("createUser", e.getMessage());
                }
            });

            loginUser(username, password);
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user" + username);
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with login", e);
                Toast.makeText(SetCollegeActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
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
                            setCollege();
                            setUserActivity();
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
            Toast.makeText(SetCollegeActivity.this, "Success", Toast.LENGTH_SHORT).show();
        });
        goMainActivity();
    }

    public void setCollege() throws JSONException {
        User mNewUser = CometChat.getLoggedInUser();
        mMetadata.put("college", mSelectedCollege);
        mNewUser.setMetadata(mMetadata);
        updateUser(mNewUser);
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

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}