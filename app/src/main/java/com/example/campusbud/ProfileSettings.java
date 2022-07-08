package com.example.campusbud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
    public EditText etInterests;
    public EditText etActivities;
    public EditText etBio;
    public Button btnSubmit;
    public ImageView ivCreatePicture;
    public TextView tvCleanliness;
    public TextView tvSmoke;
    public TextView tvDrink;
    public TextView tvRoomUse;
    public TextView tvTimeMorning;
    public TextView tvTimeNight;
    public ImageView ivProfileImage1;
    public ImageView ivProfileImage2;
    public ImageView ivProfileImage3;

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private File photoFile;
    public String photoFileName = "photo.jpg";

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
    public String interests;
    public String activities;
    public String bio;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        user = CometChat.getLoggedInUser();

        radioYearGroup = findViewById(R.id.radioYearGroup);
        ivCreatePicture = findViewById(R.id.ivCreatePicture);
        etName = findViewById(R.id.etName);
        etMajor = findViewById(R.id.etMajor);
        etTimeMorning = findViewById(R.id.etTimeMorning);
        etTimeNight = findViewById(R.id.etTimeNight);
        btnSubmit = findViewById(R.id.btnSubmit);
        roommateSwitch = findViewById(R.id.roommateSwitch);
        rgCleanliness = findViewById(R.id.rgCleanliness);
        rgSmoke = findViewById(R.id.rgSmoke);
        rgDrink = findViewById(R.id.rgDrink);
        rgRoomUse = findViewById(R.id.rgRoomUse);
        rgGender = findViewById(R.id.rgGender);
        etInterests = findViewById(R.id.etInterests);
        etActivities = findViewById(R.id.etActivities);
        etBio = findViewById(R.id.etBio);
        ivProfileImage1 = findViewById(R.id.ivProfileImage1);
        ivProfileImage2 = findViewById(R.id.ivProfileImage2);
        ivProfileImage3 = findViewById(R.id.ivProfileImage3);

        /*roommateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (roommateSwitch.isChecked()) {
                    switchState = true;
                } else {
                    switchState = false;
                }
            }
        });*/

        radioYearGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioYearButton = (RadioButton) findViewById(checkedId);
                year = radioYearButton.getText().toString();
            }
        });

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

        ivCreatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu();
            }
        });

        ivProfileImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu();
            }
        });

        ivProfileImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu();
            }
        });

        ivProfileImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = etName.getText().toString();
                major = etMajor.getText().toString();
                timeSleep = etTimeNight.getText().toString();
                timeWake = etTimeMorning.getText().toString();
                interests = etInterests.getText().toString();
                activities = etActivities.getText().toString();
                bio = etBio.getText().toString();

                try {
                    metadata.put("name", name);
                    metadata.put("year", year);
                    metadata.put("major", major);
                    metadata.put("gender", gender);
                    roommateProfile.put("time_sleep", timeSleep);
                    roommateProfile.put("time_wake", timeWake);
                    roommateProfile.put("interests", interests);
                    roommateProfile.put("activities", activities);
                    roommateProfile.put("bio", bio);
                    roommateProfile.put("cleanliness", cleanliness);
                    roommateProfile.put("if_smoke", ifSmoke);
                    roommateProfile.put("if_drink", ifDrink);
                    roommateProfile.put("room_use", roomUse);
                    roommateProfileArray.put(roommateProfile);
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

    public void optionsMenu() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    launchCamera();
                    //saveImage(user, photoFile);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        }); builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivCreatePicture.setImageBitmap(takenImage);
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        //String authority = this.getPackageName() + ".fileprovider";
        //Uri fileProvider = FileProvider.getUriForFile(this, authority, photoFile);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
}