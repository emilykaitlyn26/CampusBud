package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetCollegeActivity extends AppCompatActivity {

    public ArrayAdapter<String> uniadapter;
    public List<ParseObject> listColleges;
    public List<String> universityNames;
    public String stateID;
    public ParseObject stateObject;
    public String selectedCollege;
    public Button btnSignUp;
    public JSONObject metadata = new JSONObject();
    //public List<List<Integer>> userActivity;
    public JSONArray userActivity = new JSONArray();
    public JSONArray userYearActivity = new JSONArray();
    public JSONObject userYearValues = new JSONObject();
    public JSONArray userCleanlinessActivity = new JSONArray();
    public JSONObject userCleanlinessValues = new JSONObject();
    public JSONArray userSmokingActivity = new JSONArray();
    public JSONObject userSmokingValues = new JSONObject();
    public JSONArray userDrinkingActivity = new JSONArray();
    public JSONObject userDrinkingValues = new JSONObject();
    public JSONArray userRoomUseActivity = new JSONArray();
    public JSONObject userRoomUseValues = new JSONObject();
    public JSONArray userSleepTimeActivity = new JSONArray();
    public JSONArray userWakeTimeActivity = new JSONArray();
    public JSONArray userRates = new JSONArray();
    public JSONObject userRateValues = new JSONObject();

    private static final String TAG = "SetCollegeActivity";
    public static final String authKey = "c523b47dfef8a387d934b40bbcf7d7bc5fe2c0ee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_college);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String state = getIntent().getStringExtra("state");
        ArrayList<State> list = (ArrayList<State>) getIntent().getSerializableExtra("list");

        listColleges = new ArrayList<>();
        universityNames = new ArrayList<>();
        /*userActivity = new ArrayList<>(
                Arrays.asList(Arrays.asList(0, 0, 0, 0), Arrays.asList(0, 0, 0, 0), Arrays.asList(0, 0, 0), Arrays.asList(0, 0, 0), Arrays.asList(0, 0, 0, 0), Arrays.asList(0, 0, 0, 0), Arrays.asList(0, 0, 0, 0))
        );*/

        if (list.size() == 56) {
            for (int i = 0; i < list.size(); i++) {
                stateObject = list.get(i);
                String stateName = stateObject.getString("name");
                if (stateName.equals(state)) {
                    stateID = stateObject.getObjectId();
                    break;
                }
            }

            ParseRelation<ParseObject> relation = stateObject.getRelation("Universities");
            ParseQuery<ParseObject> query = relation.getQuery();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issue getting colleges");
                        return;
                    }
                    listColleges.addAll(objects);
                    for (int i = 0; i < listColleges.size(); i++) {
                        ParseObject collegeObject = listColleges.get(i);
                        String name = collegeObject.getString("name");
                        universityNames.add(name);
                    }
                    uniadapter.notifyDataSetChanged();
                }
            });

            uniadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, universityNames);
            AutoCompleteTextView uniTextView = (AutoCompleteTextView) findViewById(R.id.chooseCollege);
            uniTextView.setThreshold(3);
            uniTextView.setAdapter(uniadapter);
            uniTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedCollege = (String) parent.getItemAtPosition(position);
                }
            });
        }

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signUpUser(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void signUpUser(String username, String password) throws IOException {
        Log.i(TAG, "Attempting to sign up user" + username);
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with sign up", e);
                Toast.makeText(this, "Issue with sign up", Toast.LENGTH_SHORT).show();
                return;
            }
            String UID = parseUser.getObjectId();
            User user = new User();
            user.setUid(UID);
            user.setName(username);

            CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
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
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(SetCollegeActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser parseUser = ParseUser.getCurrentUser();
                String UID = parseUser.getObjectId();

                if (CometChat.getLoggedInUser() == null) {
                    CometChat.login(UID, authKey, new CometChat.CallbackListener<User>() {
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
            }
        });
        goMainActivity();
    }

    public void setCollege() throws JSONException {
        User newUser = CometChat.getLoggedInUser();
        metadata.put("college", selectedCollege);
        newUser.setMetadata(metadata);
        updateUser(newUser);
    }

    public void setUserActivity() throws JSONException {
        User newUser = CometChat.getLoggedInUser();
        metadata.put("num_refreshed", 0);
        userYearValues.put("freshman", 0);
        userYearValues.put("sophomore", 0);
        userYearValues.put("junior", 0);
        userYearValues.put("senior", 0);
        userYearActivity.put(userYearValues);
        userCleanlinessValues.put("organized", 0);
        userCleanlinessValues.put("casual", 0);
        userCleanlinessValues.put("occasionally_messy", 0);
        userCleanlinessValues.put("messy", 0);
        userCleanlinessActivity.put(userCleanlinessValues);
        userSmokingValues.put("yes", 0);
        userSmokingValues.put("sometimes", 0);
        userSmokingValues.put("no", 0);
        userSmokingActivity.put(userSmokingValues);
        userDrinkingValues.put("yes", 0);
        userDrinkingValues.put("sometimes", 0);
        userDrinkingValues.put("no", 0);
        userDrinkingActivity.put(userDrinkingValues);
        userRoomUseValues.put("social_space", 0);
        userRoomUseValues.put("study_space", 0);
        userRoomUseValues.put("sleeping_space", 0);
        userRoomUseValues.put("all_above", 0);
        userRoomUseActivity.put(userRoomUseValues);
        userActivity.put(userYearActivity);
        userActivity.put(userCleanlinessActivity);
        userActivity.put(userSmokingActivity);
        userActivity.put(userDrinkingActivity);
        userActivity.put(userRoomUseActivity);
        metadata.put("user_activity", userActivity);

        userRateValues.put("year_max", 0.7);
        userRateValues.put("year_average", 0.5);
        userRateValues.put("year_below", 0.3);
        userRateValues.put("year_lowest", 0.1);
        userRateValues.put("clean_max", 0.7);
        userRateValues.put("clean_average", 0.5);
        userRateValues.put("clean_below", 0.3);
        userRateValues.put("clean_lowest", 0.1);
        userRateValues.put("smoke_max", 0.7);
        userRateValues.put("smoke_average", 0.5);
        userRateValues.put("smoke_below", 0.3);
        userRateValues.put("drink_max", 0.7);
        userRateValues.put("drink_average", 0.5);
        userRateValues.put("drink_below", 0.3);
        userRateValues.put("room_max", 0.7);
        userRateValues.put("room_average", 0.5);
        userRateValues.put("room_below", 0.3);
        userRateValues.put("room_lowest", 0.1);
        userRates.put(userRateValues);
        metadata.put("rate_values", userRates);
        newUser.setMetadata(metadata);
        updateUser(newUser);
    }

    public void updateUser(User user) {
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