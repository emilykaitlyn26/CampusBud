package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.fragments.ProfileFragment;
import com.parse.Parse;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileSettings extends AppCompatActivity {

    EditText etName;
    EditText etMajor;
    EditText etNewUsername;
    EditText etNewPassword;
    Button btnSubmit;

    String TAG = "ProfileSettings";

    public JSONObject metadata = new JSONObject();

    RadioGroup radioYearGroup;
    RadioButton radioYearButton;

    String year;
    String name;
    String major;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        User user = CometChat.getLoggedInUser();
        ParseUser parseUser = ParseUser.getCurrentUser();

        radioYearGroup = (RadioGroup) findViewById(R.id.radioYearGroup);
        etName = findViewById(R.id.etName);
        etMajor = findViewById(R.id.etMajor);
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        /*if (etNewUsername.getText() != null) {
            username = etNewUsername.getText().toString();
            if (!parseUser.getUsername().equals(username)) {
                parseUser.setUsername(username);
            }
        }*/

        /*if (etNewPassword.getText() != null) {
            password = etNewPassword.getText().toString();
            if (!parseUser.getString("Password").equals(password)) {
                parseUser.setPassword(password);
            }
        }*/

        radioYearGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioYearButton = (RadioButton) findViewById(checkedId);
                year = radioYearButton.getText().toString();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                             //createProfileData(metadata);

                name = etName.getText().toString();
                major = etMajor.getText().toString();
                                             //});

                try {
                    metadata.put("name", name);
                    metadata.put("year", year);
                    metadata.put("major", major);
                    Log.d(TAG, "metadata: " + metadata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                user.setMetadata(metadata);
                updateUser(user);
                finish();
            }
        });
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
}