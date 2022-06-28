package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileSettings extends AppCompatActivity {

    EditText etName;
    EditText etYear;
    EditText etMajor;

    RadioGroup radioYearGroup;
    RadioButton radioYearButton;

    User user = CometChat.getLoggedInUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        radioYearGroup = (RadioGroup) findViewById(R.id.radioYearGroup);
        etName = findViewById(R.id.etName);
        etYear = findViewById(R.id.etYear);
        etMajor = findViewById(R.id.etMajor);

        JSONObject metadata = new JSONObject();

        String name = etName.getText().toString();
        String year = etYear.getText().toString();
        String major = etMajor.getText().toString();

        try {
            metadata.put("UserName", name);
            metadata.put("UserYear", year);
            metadata.put("UserMajor", major);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user.setMetadata(metadata);
    }
}