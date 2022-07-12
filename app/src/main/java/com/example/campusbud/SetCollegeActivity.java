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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            goMainActivity();
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
    }

    public void setCollege() throws JSONException {
        User newUser = CometChat.getLoggedInUser();
        metadata.put("college", selectedCollege);
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