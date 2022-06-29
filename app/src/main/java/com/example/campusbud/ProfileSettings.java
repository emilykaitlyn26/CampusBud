package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.fragments.ProfileFragment;
import com.parse.Parse;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileSettings extends AppCompatActivity {

    public EditText etName;
    public EditText etMajor;
    public EditText etNewUsername;
    public EditText etNewPassword;
    public EditText etTimeMorning;
    public EditText etTimeNight;
    public Button btnSubmit;
    public ImageView ivCreatePicture;
    public TextView tvCleanliness;
    public TextView tvSmoke;
    public TextView tvDrink;
    public TextView tvRoomUse;
    public TextView tvTimeMorning;
    public TextView tvTimeNight;

    public TimePickerDialog timePickerDialog;

    public User user;

    private final String TAG = "ProfileSettings";

    public JSONObject metadata = new JSONObject();
    public JSONArray roommateProfileArray = new JSONArray();
    public JSONObject roommateProfile = new JSONObject();

    public RadioGroup radioYearGroup;
    public RadioButton radioYearButton;
    public Switch roommateSwitch;
    public Boolean switchState = false;
    public RadioGroup rgCleanliness;
    public RadioButton rbCleanliness;
    public RadioGroup rgSmoke;
    public RadioButton rbSmoke;
    public RadioGroup rgDrink;
    public RadioButton rbDrink;
    public RadioGroup rgRoomUse;
    public RadioButton rbRoomUse;
    public RadioGroup rgGender;
    public RadioButton rbGender;

    public String year;
    public String name;
    public String major;
    public String cleanliness;
    public String ifSmoke;
    public String ifDrink;
    public String roomUse;
    public String gender;
    public String timeSleep;
    public String timeWake;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        user = CometChat.getLoggedInUser();
        ParseUser parseUser = ParseUser.getCurrentUser();

        radioYearGroup = (RadioGroup) findViewById(R.id.radioYearGroup);
        ivCreatePicture = findViewById(R.id.ivCreatePicture);
        etName = findViewById(R.id.etName);
        etMajor = findViewById(R.id.etMajor);
        etTimeMorning = findViewById(R.id.etTimeMorning);
        etTimeNight = findViewById(R.id.etTimeNight);
        //etNewUsername = findViewById(R.id.etNewUsername);
        //etNewPassword = findViewById(R.id.etNewPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        roommateSwitch = findViewById(R.id.roommateSwitch);
        tvCleanliness = findViewById(R.id.tvCleanliness);
        tvSmoke = findViewById(R.id.tvSmoke);
        tvDrink = findViewById(R.id.tvDrink);
        rgCleanliness = findViewById(R.id.rgCleanliness);
        rgSmoke = findViewById(R.id.rgSmoke);
        rgDrink = findViewById(R.id.rgDrink);
        tvRoomUse = findViewById(R.id.tvRoomUse);
        rgRoomUse = findViewById(R.id.rgRoomUse);
        rgGender = findViewById(R.id.rgGender);
        tvTimeMorning = findViewById(R.id.tvTimeMorning);
        tvTimeNight = findViewById(R.id.tvTimeNight);

        /*roommateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (roommateSwitch.isChecked()) {
                    switchState = true;
                    tvCleanliness.setVisibility(View.VISIBLE);
                    tvSmoke.setVisibility(View.VISIBLE);
                    tvDrink.setVisibility(View.VISIBLE);
                    rgCleanliness.setVisibility(View.VISIBLE);
                    rgSmoke.setVisibility(View.VISIBLE);
                    rgDrink.setVisibility(View.VISIBLE);

                } else {
                    switchState = false;
                    tvCleanliness.setVisibility(View.GONE);
                    tvSmoke.setVisibility(View.GONE);
                    tvDrink.setVisibility(View.GONE);
                    rgCleanliness.setVisibility(View.GONE);
                    rgSmoke.setVisibility(View.GONE);
                    rgDrink.setVisibility(View.GONE);
                }
            }
        });*/

        /*if (switchState == true) {
            tvCleanliness.setVisibility(View.VISIBLE);
            tvSmoke.setVisibility(View.VISIBLE);
            tvDrink.setVisibility(View.VISIBLE);
            rgCleanliness.setVisibility(View.VISIBLE);
            rgSmoke.setVisibility(View.VISIBLE);
            rgDrink.setVisibility(View.VISIBLE);
        } else {
            tvCleanliness.setVisibility(View.GONE);
            tvSmoke.setVisibility(View.GONE);
            tvDrink.setVisibility(View.GONE);
            rgCleanliness.setVisibility(View.GONE);
            rgSmoke.setVisibility(View.GONE);
            rgDrink.setVisibility(View.GONE);
        }*/

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

        //if (switchState == true) {
            rgCleanliness.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    rbCleanliness = (RadioButton) findViewById(checkedId);
                    cleanliness = rbCleanliness.getText().toString();
                }
            });

            rgSmoke.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    rbSmoke = findViewById(checkedId);
                    ifSmoke = rbSmoke.getText().toString();
                }
            });

            rgDrink.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    rbDrink = findViewById(checkedId);
                    ifDrink = rbDrink.getText().toString();
                }
            });
        //}

        rgRoomUse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbRoomUse = findViewById(checkedId);
                roomUse = rbRoomUse.getText().toString();
            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbGender = findViewById(checkedId);
                gender = rbGender.getText().toString();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = etName.getText().toString();
                major = etMajor.getText().toString();

                timeSleep = etTimeNight.getText().toString();
                timeWake = etTimeMorning.getText().toString();

                try {
                    metadata.put("name", name);
                    metadata.put("year", year);
                    metadata.put("major", major);
                    metadata.put("gender", gender);
                    metadata.put("time_sleep", timeSleep);
                    metadata.put("time_wake", timeWake);
                    roommateProfile.put("cleanliness", cleanliness);
                    roommateProfile.put("if_smoke", ifSmoke);
                    roommateProfile.put("if_drink", ifDrink);
                    roommateProfile.put("room_use", roomUse);
                    roommateProfileArray.put(roommateProfile);
                    //metadata.put("cleanliness", cleanliness);
                    metadata.put("roommate_profile", roommateProfileArray);
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