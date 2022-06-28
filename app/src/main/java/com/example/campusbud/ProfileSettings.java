package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.parse.Parse;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileSettings extends AppCompatActivity {

    EditText etName;
    EditText etMajor;
    EditText etNewUsername;
    EditText etNewPassword;

    RadioGroup radioYearGroup;
    RadioButton radioYearButton;

    String year;
    String name;
    String major;
    String username;
    String password;

    User user = CometChat.getLoggedInUser();
    ParseUser parseUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        radioYearGroup = (RadioGroup) findViewById(R.id.radioYearGroup);
        etName = findViewById(R.id.etName);
        etMajor = findViewById(R.id.etMajor);
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);

        JSONObject metadata = new JSONObject();

        name = etName.getText().toString();
        major = etMajor.getText().toString();
        /*if (etNewUsername.getText() != null) {
            username = etNewUsername.getText().toString();
            if (!parseUser.getUsername().equals(username)) {
                parseUser.setUsername(username);
            }
        }
        if (etNewPassword.getText() != null) {
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

        try {
            metadata.put("name", name);
            metadata.put("year", year);
            metadata.put("major", major);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user.setMetadata(metadata);

    }
}