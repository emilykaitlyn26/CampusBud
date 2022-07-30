package com.example.campusbud;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.models.Image;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;

import java.io.File;
import java.util.Objects;

public class ImagesActivity extends AppCompatActivity {

    private ImageView mIvProfile;
    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;

    private File mPhotoFile1;
    private File mPhotoFile2;
    private File mPhotoFile3;
    private File mPhotoFileProfile;

    //private List<Image> mImages;

    private static final String TAG = "ImagesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        ParseUser parseUser = ParseUser.getCurrentUser();

        mIvProfile = findViewById(R.id.ivNewProfilePic);
        mIvImage1 = findViewById(R.id.ivNewPicture1);
        mIvImage2 = findViewById(R.id.ivNewPicture2);
        mIvImage3 = findViewById(R.id.ivNewPicture3);
        Button mBtnImagesContinue = findViewById(R.id.btnContinueImages);

        Context context1 = mIvImage1.getContext();
        mIvImage1.setColorFilter(ContextCompat.getColor(context1, R.color.grey));
        Context context2 = mIvImage2.getContext();
        mIvImage2.setColorFilter(ContextCompat.getColor(context2, R.color.grey));
        Context context3 = mIvImage3.getContext();
        mIvImage3.setColorFilter(ContextCompat.getColor(context3, R.color.grey));

        mIvProfile.setOnClickListener(v -> optionsMenu("Profile"));

        mIvImage1.setOnClickListener(v -> optionsMenu("Image1"));

        mIvImage2.setOnClickListener(v -> optionsMenu("Image2"));

        mIvImage3.setOnClickListener(v -> optionsMenu("Image3"));

        mBtnImagesContinue.setOnClickListener(v -> {
            try {
                saveImages(parseUser, mPhotoFileProfile, mPhotoFile1, mPhotoFile2, mPhotoFile3);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), ProfileSettings.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveImages(ParseUser currentUser, File profileFile, File file1, File file2, File file3) throws JSONException {
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
        updatePFPCometChat();
    }


    private void updatePFPCometChat(){
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        query.include(Image.KEY_USER);
        query.whereEqualTo(Image.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground((objects, e) -> {
            if (e != null) {
                e.printStackTrace();
                return;
            }
            if (objects.size() != 0) {
                try {
                    int mIndex = objects.size() - 1;
                    CometChat.getLoggedInUser().getMetadata().put("ProfilePic", objects.get(mIndex).getProfileImageUrl().getUrl());
                    updateUser(CometChat.getLoggedInUser());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                Log.e("Img link:" , "broken");
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
                mIvProfile.setImageBitmap(mTakenImage);
                mIvProfile.setRotation((float) 90.0);
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
                mIvProfile.setImageURI(mImageUri);
            }
        }
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFile1.getAbsolutePath());
                mIvImage1.clearColorFilter();
                mIvImage1.setImageBitmap(mTakenImage);
                mIvImage1.setRotation((float) 90.0);
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
                mIvImage1.clearColorFilter();
                mPhotoFile1 = new File(mCurrentPhotoPath);
                mIvImage1.setImageURI(mImageUri);
            }
        }
        if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFile2.getAbsolutePath());
                mIvImage2.clearColorFilter();
                mIvImage2.setImageBitmap(mTakenImage);
                mIvImage2.setRotation((float) 90.0);
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
                mIvImage2.clearColorFilter();
                mPhotoFile2 = new File(mCurrentPhotoPath);
                mIvImage2.setImageURI(mImageUri);
            }
        }
        if (requestCode == 7) {
            if (resultCode == RESULT_OK) {
                Bitmap mTakenImage = BitmapFactory.decodeFile(mPhotoFile3.getAbsolutePath());
                mIvImage3.clearColorFilter();
                mIvImage3.setImageBitmap(mTakenImage);
                mIvImage3.setRotation((float) 90.0);
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
                mIvImage3.clearColorFilter();
                mPhotoFile3 = new File(mCurrentPhotoPath);
                mIvImage3.setImageURI(mImageUri);
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

}