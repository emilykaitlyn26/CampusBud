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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.fragments.ProfileFragment;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
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
import java.util.Objects;

public class ProfileSettings extends AppCompatActivity {

    public EditText etName;
    public EditText etMajor;
    public EditText etTimeMorning;
    public EditText etTimeNight;
    public EditText etInterests;
    public EditText etActivities;
    public EditText etBio;
    public Button btnSubmit;
    public ImageView ivCreatePicture;
    public ImageView ivProfileImage1;
    public ImageView ivProfileImage2;
    public ImageView ivProfileImage3;

    private File photoFile1;
    private File photoFile2;
    private File photoFile3;
    private File photoFileProfile;
    public String profilePhotoName = "profilephoto.jpg";
    public String photo1Name = "photo1.jpg";
    public String photo2Name = "photo2.jpg";
    public String photo3Name = "photo3.jpg";

    public User user;
    public ParseUser parseUser;

    private final String TAG = "ProfileSettings";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        user = CometChat.getLoggedInUser();
        parseUser = ParseUser.getCurrentUser();

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

        roommateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchState = roommateSwitch.isChecked();
            }
        });

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
                optionsMenu("Profile");
            }
        });

        ivProfileImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu("Image1");
            }
        });

        ivProfileImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu("Image2");
            }
        });

        ivProfileImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsMenu("Image3");
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
                JSONObject metadata = user.getMetadata();

                try {
                    metadata.put("name", name);
                    metadata.put("year", year);
                    metadata.put("major", major);
                    metadata.put("gender", gender);
                    metadata.put("ifSwitched", switchState);
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
                updateUser(user);
                saveImages(parseUser, photoFileProfile, photoFile1, photoFile2, photoFile3);
                finish();
            }
        });
    }

    public void saveImages(ParseUser currentUser, File profileFile, File file1, File file2, File file3) {
        Image image = new Image();
        image.setProfileImage(new ParseFile(profileFile));
        image.setImage1(new ParseFile(file1));
        image.setImage2(new ParseFile(file2));
        image.setImage3(new ParseFile(file3));
        image.setUser(currentUser);
        image.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving", e);
            }
            Log.i(TAG, "Post save was successful!");
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

    public void optionsMenu(String image) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    launchCamera(image);
                } else if (options[item].equals("Choose from Gallery")) {
                    getImageFromAlbum(image);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        }); builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFileProfile.getAbsolutePath());
                ivCreatePicture.setImageBitmap(takenImage);
                ivCreatePicture.setRotation((float) 90.0);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                /*String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String currentPhotoPath = cursor.getString(columnIndex);
                cursor.close();
                File image = new File(currentPhotoPath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap selectedImage = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
                ivCreatePicture.setImageBitmap(selectedImage);*/
                ivCreatePicture.setImageURI(imageUri);
            }
        }
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile1.getAbsolutePath());
                ivProfileImage1.setImageBitmap(takenImage);
                ivProfileImage1.setRotation((float) 90.0);
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                ivProfileImage1.setImageURI(imageUri);
            }
        }
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile2.getAbsolutePath());
                ivProfileImage2.setImageBitmap(takenImage);
                ivProfileImage2.setRotation((float) 90.0);
            }
        } else if (requestCode == 6) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                ivProfileImage2.setImageURI(imageUri);
            }
        }
        if (requestCode == 7) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile3.getAbsolutePath());
                ivProfileImage3.setImageBitmap(takenImage);
                ivProfileImage3.setRotation((float) 90.0);
            }
        } else if (requestCode == 8) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();
                ivProfileImage3.setImageURI(imageUri);
            }
        }
    }

    private void getImageFromAlbum(String image) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (image.equals("Profile")) {
            startActivityForResult(intent, 2);
        } else if (image.equals("Image1")) {
            startActivityForResult(intent, 4);
        } else if (image.equals("Image2")) {
            startActivityForResult(intent, 6);
        } else if (image.equals("Image3")) {
            startActivityForResult(intent, 8);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void launchCamera(String image) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            if (image.equals("Profile")) {
                photoFileProfile = getPhotoFileUri(profilePhotoName);
                Uri fileProviderp = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", photoFileProfile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProviderp);
                startActivityForResult(intent, 30);
            } else if (image.equals("Image1")) {
                photoFile1 = getPhotoFileUri(photo1Name);
                Uri fileProvider1 = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", photoFile1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider1);
                startActivityForResult(intent, 3);
            } else if (image.equals("Image2")) {
                photoFile2 = getPhotoFileUri(photo2Name);
                Uri fileProvider2 = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", photoFile2);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider2);
                startActivityForResult(intent, 5);
            } else if (image.equals("Image3")) {
                photoFile3 = getPhotoFileUri(photo3Name);
                Uri fileProvider3 = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", photoFile3);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider3);
                startActivityForResult(intent, 7);
            }
        }
    }
}