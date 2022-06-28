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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.fragments.ProfileFragment;
import com.parse.Parse;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileSettings extends AppCompatActivity {

    public EditText etName;
    public EditText etMajor;
    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnSubmit;
    public ImageView ivCreatePicture;
    public TextView tvCleanliness;
    public TextView tvSmoke;
    public TextView tvDrink;

    private final String TAG = "ProfileSettings";

    public JSONObject metadata = new JSONObject();

    public RadioGroup radioYearGroup;
    public RadioButton radioYearButton;
    public Switch roommateSwitch;
    public Boolean switchState;
    public RadioGroup rgCleanliness;
    public RadioButton rbCleanliness;
    public RadioGroup rgSmoke;
    public RadioButton rbSmoke;
    public RadioGroup rgDrink;
    public RadioButton rbDrink;

    public String year;
    public String name;
    public String major;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        User user = CometChat.getLoggedInUser();
        ParseUser parseUser = ParseUser.getCurrentUser();

        radioYearGroup = (RadioGroup) findViewById(R.id.radioYearGroup);
        ivCreatePicture = findViewById(R.id.ivCreatePicture);
        etName = findViewById(R.id.etName);
        etMajor = findViewById(R.id.etMajor);
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        roommateSwitch = findViewById(R.id.roommateSwitch);
        tvCleanliness = findViewById(R.id.tvCleanliness);
        tvSmoke = findViewById(R.id.tvSmoke);
        tvDrink = findViewById(R.id.tvDrink);
        rgCleanliness = findViewById(R.id.rgCleanliness);
        rgSmoke = findViewById(R.id.rgSmoke);
        rgDrink = findViewById(R.id.rgDrink);

        roommateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        });

        switchState = roommateSwitch.isChecked();

        if (switchState == true) {
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
        }

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

                name = etName.getText().toString();
                major = etMajor.getText().toString();

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