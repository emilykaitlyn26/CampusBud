package com.example.campusbud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class ProfileSettings extends AppCompatActivity {

    private EditText mEtName;
    private EditText mEtMajor;
    private EditText mEtInterests;
    private EditText mEtActivities;
    private EditText mEtBio;
    private ImageView mIvCreatePicture;
    private ImageView mIvProfileImage1;
    private ImageView mIvProfileImage2;
    private ImageView mIvProfileImage3;

    private File mPhotoFile1;
    private File mPhotoFile2;
    private File mPhotoFile3;
    private File mPhotoFileProfile;

    private User mUser;
    private ParseUser mParseUser;

    private final String TAG = "ProfileSettings";

    private JSONArray mRoommateProfileArray = new JSONArray();
    private JSONObject mRoommateProfile = new JSONObject();

    private String[] mTimes = {"12:00am", "1:00am", "2:00am", "3:00am", "4:00am", "5:00am", "6:00am", "7:00am", "8:00am", "9:00am", "10:00am", "11:00am", "12:00pm", "1:00pm", "2:00pm", "3:00pm", "4:00pm", "5.00pm", "6:00pm", "7.00pm", "8:00pm", "9:00pm", "10:00pm", "11:00pm"};

    private RadioButton mRadioYearButton;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mRoommateSwitch;
    private Boolean mSwitchState = false;
    private RadioButton mRbCleanliness;
    private RadioButton mRbSmoke;
    private RadioButton mRbDrink;
    private RadioButton mRbRoomUse;
    private RadioButton mRbGender;
    private Button mBtnSettingsContinue;

    private String mYear;
    private String mName;
    private String mMajor;
    private String mCleanliness;
    private String mIfSmoke;
    private String mIfDrink;
    private String mRoomUse;
    private String mGender;
    private String mTimeSleep;
    private String mTimeWake;
    private String mInterests;
    private String mActivities;
    private String mBio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mUser = CometChat.getLoggedInUser();
        mParseUser = ParseUser.getCurrentUser();

        AutoCompleteTextView mSleepTextView = (AutoCompleteTextView) findViewById(R.id.etTimeNight);
        AutoCompleteTextView mWakeTextView = (AutoCompleteTextView) findViewById(R.id.etTimeMorning);
        ArrayAdapter<String> mSleeptimeadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mTimes);
        ArrayAdapter<String> mWaketimeadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mTimes);
        mSleepTextView.setThreshold(1);
        mSleepTextView.setAdapter(mSleeptimeadapter);
        mWakeTextView.setThreshold(1);
        mWakeTextView.setAdapter(mWaketimeadapter);

        RadioGroup mRadioYearGroup = findViewById(R.id.radioYearGroup);
        mIvCreatePicture = findViewById(R.id.ivCreatePicture);
        mEtName = findViewById(R.id.etName);
        mEtMajor = findViewById(R.id.etMajor);
        Button mBtnSettingsContinue = findViewById(R.id.btnSettingsContinue);
        mRoommateSwitch = findViewById(R.id.roommateSwitch);
        RadioGroup mRgCleanliness = findViewById(R.id.rgCleanliness);
        RadioGroup mRgSmoke = findViewById(R.id.rgSmoke);
        RadioGroup mRgDrink = findViewById(R.id.rgDrink);
        RadioGroup mRgRoomUse = findViewById(R.id.rgRoomUse);
        RadioGroup mRgGender = findViewById(R.id.rgGender);
        //mEtInterests = findViewById(R.id.etInterests);
        //mEtActivities = findViewById(R.id.etActivities);
        //mEtBio = findViewById(R.id.etBio);
        mIvProfileImage1 = findViewById(R.id.ivProfileImage1);
        mIvProfileImage2 = findViewById(R.id.ivProfileImage2);
        mIvProfileImage3 = findViewById(R.id.ivProfileImage3);
        //mBtnInterests = findViewById(R.id.btnInterests);

        mRoommateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mSwitchState = mRoommateSwitch.isChecked());

        mRadioYearGroup.setOnCheckedChangeListener((group, checkedId) -> {
            mRadioYearButton = (RadioButton) findViewById(checkedId);
            mYear = mRadioYearButton.getText().toString();
        });

        mRgCleanliness.setOnCheckedChangeListener((group, checkedId) -> {
            mRbCleanliness = (RadioButton) findViewById(checkedId);
            mCleanliness = mRbCleanliness.getText().toString();
        });

        mRgSmoke.setOnCheckedChangeListener((group, checkedId) -> {
            mRbSmoke = findViewById(checkedId);
            mIfSmoke = mRbSmoke.getText().toString();
        });

        mRgDrink.setOnCheckedChangeListener((group, checkedId) -> {
            mRbDrink = findViewById(checkedId);
            mIfDrink = mRbDrink.getText().toString();
        });

        mRgRoomUse.setOnCheckedChangeListener((group, checkedId) -> {
            mRbRoomUse = findViewById(checkedId);
            mRoomUse = mRbRoomUse.getText().toString();
        });

        mRgGender.setOnCheckedChangeListener((group, checkedId) -> {
            mRbGender = findViewById(checkedId);
            mGender = mRbGender.getText().toString();
        });

        mWakeTextView.setOnItemClickListener((parent, view, position, id) -> mTimeWake = (String) parent.getItemAtPosition(position));

        mSleepTextView.setOnItemClickListener((parent, view, position, id) -> mTimeSleep = (String) parent.getItemAtPosition(position));

        mIvCreatePicture.setOnClickListener(v -> optionsMenu("Profile"));

        mIvProfileImage1.setOnClickListener(v -> optionsMenu("Image1"));

        mIvProfileImage2.setOnClickListener(v -> optionsMenu("Image2"));

        mIvProfileImage3.setOnClickListener(v -> optionsMenu("Image3"));

        mBtnSettingsContinue.setOnClickListener(v -> {

            mName = mEtName.getText().toString();
            mMajor = mEtMajor.getText().toString();
            //mInterests = mEtInterests.getText().toString();
            //mActivities = mEtActivities.getText().toString();
            //mBio = mEtBio.getText().toString();
            JSONObject metadata = mUser.getMetadata();

            try {
                metadata.put("name", mName);
                metadata.put("year", mYear);
                metadata.put("major", mMajor);
                metadata.put("gender", mGender);
                metadata.put("ifSwitched", mSwitchState);
                mRoommateProfile.put("time_sleep", mTimeSleep);
                mRoommateProfile.put("time_wake", mTimeWake);
                //mRoommateProfile.put("interests", mInterests);
                //mRoommateProfile.put("activities", mActivities);
                //mRoommateProfile.put("bio", mBio);
                mRoommateProfile.put("cleanliness", mCleanliness);
                mRoommateProfile.put("if_smoke", mIfSmoke);
                mRoommateProfile.put("if_drink", mIfDrink);
                mRoommateProfile.put("room_use", mRoomUse);
                mRoommateProfileArray.put(mRoommateProfile);
                metadata.put("roommate_profile", mRoommateProfileArray);
                Log.d(TAG, "metadata: " + metadata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUser(mUser);
            saveImages(mParseUser, mPhotoFileProfile, mPhotoFile1, mPhotoFile2, mPhotoFile3);
            Intent intent = new Intent(this, InterestsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveImages(ParseUser currentUser, File profileFile, File file1, File file2, File file3) {
        Image mImage = new Image();
        mImage.setProfileImage(new ParseFile(profileFile));
        mImage.setImage1(new ParseFile(file1));
        mImage.setImage2(new ParseFile(file2));
        mImage.setImage3(new ParseFile(file3));
        mImage.setUser(currentUser);
        mImage.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving", e);
            }
            Log.i(TAG, "Post save was successful!");
        });
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

    public void optionsMenu(String image) {
        final CharSequence[] mOptions = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Add Photo");
        mBuilder.setItems(mOptions, (dialog, item) -> {
            if (mOptions[item].equals("Take Photo")) {
                launchCamera(image);
            } else if (mOptions[item].equals("Choose from Gallery")) {
                getImageFromAlbum(image);
            } else if (mOptions[item].equals("Cancel")) {
                dialog.dismiss();
            }
        }); mBuilder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFileProfile.getAbsolutePath());
                mIvCreatePicture.setImageBitmap(mTakenImage);
                mIvCreatePicture.setRotation((float) 90.0);
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK && data != null) {
                Uri mImageUri = data.getData();
                String[] mFilePathColumn = {MediaStore.Images.Media.DATA};
                Cursor mCursor = getContentResolver().query(mImageUri, mFilePathColumn, null, null, null);
                mCursor.moveToFirst();
                int mColumnIndex = mCursor.getColumnIndex(mFilePathColumn[0]);
                String mCurrentPhotoPath = mCursor.getString(mColumnIndex);
                mCursor.close();
                mPhotoFileProfile = new File(mCurrentPhotoPath);
                mIvCreatePicture.setImageURI(mImageUri);
            }
        }
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFile1.getAbsolutePath());
                mIvProfileImage1.setImageBitmap(mTakenImage);
                mIvProfileImage1.setRotation((float) 90.0);
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_OK && data != null) {
                Uri mImageUri = data.getData();
                String[] mFilePathColumn = {MediaStore.Images.Media.DATA};
                Cursor mCursor = getContentResolver().query(mImageUri, mFilePathColumn, null, null, null);
                mCursor.moveToFirst();
                int columnIndex = mCursor.getColumnIndex(mFilePathColumn[0]);
                String mCurrentPhotoPath = mCursor.getString(columnIndex);
                mCursor.close();
                mPhotoFile1 = new File(mCurrentPhotoPath);
                mIvProfileImage1.setImageURI(mImageUri);
            }
        }
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFile2.getAbsolutePath());
                mIvProfileImage2.setImageBitmap(mTakenImage);
                mIvProfileImage2.setRotation((float) 90.0);
            }
        } else if (requestCode == 6) {
            if (resultCode == RESULT_OK && data != null) {
                Uri mImageUri = data.getData();
                String[] mFilePathColumn = {MediaStore.Images.Media.DATA};
                Cursor mCursor = getContentResolver().query(mImageUri, mFilePathColumn, null, null, null);
                mCursor.moveToFirst();
                int columnIndex = mCursor.getColumnIndex(mFilePathColumn[0]);
                String mCurrentPhotoPath = mCursor.getString(columnIndex);
                mCursor.close();
                mPhotoFile2 = new File(mCurrentPhotoPath);
                mIvProfileImage2.setImageURI(mImageUri);
            }
        }
        if (requestCode == 7) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFile3.getAbsolutePath());
                mIvProfileImage3.setImageBitmap(mTakenImage);
                mIvProfileImage3.setRotation((float) 90.0);
            }
        } else if (requestCode == 8) {
            if (resultCode == RESULT_OK && data != null) {
                Uri mImageUri = data.getData();
                String[] mFilePathColumn = {MediaStore.Images.Media.DATA};
                Cursor mCursor = getContentResolver().query(mImageUri, mFilePathColumn, null, null, null);
                mCursor.moveToFirst();
                int mColumnIndex = mCursor.getColumnIndex(mFilePathColumn[0]);
                String mCurrentPhotoPath = mCursor.getString(mColumnIndex);
                mCursor.close();
                mPhotoFile3 = new File(mCurrentPhotoPath);
                mIvProfileImage3.setImageURI(mImageUri);
            }
        }
    }

    private void getImageFromAlbum(String image) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        switch (image) {
            case "Profile":
                startActivityForResult(intent, 2);
                break;
            case "Image1":
                startActivityForResult(intent, 4);
                break;
            case "Image2":
                startActivityForResult(intent, 6);
                break;
            case "Image3":
                startActivityForResult(intent, 8);
                break;
        }
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void launchCamera(String image) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            switch (image) {
                case "Profile":
                    String mProfilePhotoName = "profilephoto.jpg";
                    mPhotoFileProfile = getPhotoFileUri(mProfilePhotoName);
                    Uri mFileProviderp = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", mPhotoFileProfile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileProviderp);
                    startActivityForResult(intent, 30);
                    break;
                case "Image1":
                    String mPhoto1Name = "photo1.jpg";
                    mPhotoFile1 = getPhotoFileUri(mPhoto1Name);
                    Uri mFileProvider1 = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileProvider1);
                    startActivityForResult(intent, 3);
                    break;
                case "Image2":
                    String mPhoto2Name = "photo2.jpg";
                    mPhotoFile2 = getPhotoFileUri(mPhoto2Name);
                    Uri mFileProvider2 = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile2);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileProvider2);
                    startActivityForResult(intent, 5);
                    break;
                case "Image3":
                    String mPhoto3Name = "photo3.jpg";
                    mPhotoFile3 = getPhotoFileUri(mPhoto3Name);
                    Uri mFileProvider3 = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", mPhotoFile3);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileProvider3);
                    startActivityForResult(intent, 7);
                    break;
            }
        }
    }
}