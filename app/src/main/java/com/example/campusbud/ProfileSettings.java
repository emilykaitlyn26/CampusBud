package com.example.campusbud;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.models.Image;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileSettings extends AppCompatActivity {

    private ImageView mIvCurrentProfileImage;
    private ImageView mIvCurrentImage1;
    private ImageView mIvCurrentImage2;
    private ImageView mIvCurrentImage3;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mSwitchCurrentProfile;

    private User mUser;

    private List<Image> mImages;
    private Boolean mSwitchState;
    private JSONObject mRoommateData = new JSONObject();

    private static final String TAG = "ProfileSettings";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mUser = CometChat.getLoggedInUser();

        mImages = new ArrayList<>();
        queryImages();

        mIvCurrentProfileImage = findViewById(R.id.ivCurrentProfileImage);
        mIvCurrentImage1 = findViewById(R.id.ivCurrentImage1);
        mIvCurrentImage2 = findViewById(R.id.ivCurrentImage2);
        mIvCurrentImage3 = findViewById(R.id.ivCurrentImage3);
        TextView mTvCurrentActivity1 = findViewById(R.id.tvCurrentActivity1);
        TextView mTvCurrentActivity2 = findViewById(R.id.tvCurrentActivity2);
        TextView mTvCurrentActivity3 = findViewById(R.id.tvCurrentActivity3);
        TextView mTvCurrentBio = findViewById(R.id.tvCurrentBio);
        TextView mTvCurrentName = findViewById(R.id.tvCurrentName);
        TextView mTvCurrentYear = findViewById(R.id.tvCurrentYear);
        TextView mTvCurrentMajor = findViewById(R.id.tvCurrentMajor);
        TextView mTvCurrentInterest1 = findViewById(R.id.tvCurrentInterest1);
        TextView mTvCurrentInterest2 = findViewById(R.id.tvCurrentInterest2);
        TextView mTvCurrentInterest3 = findViewById(R.id.tvCurrentInterest3);
        TextView mTvCurrentCleanliness = findViewById(R.id.tvCurrentCleanliness);
        TextView mTvCurrentSmoking = findViewById(R.id.tvCurrentSmoking);
        TextView mTvCurrentDrinking = findViewById(R.id.tvCurrentDrinking);
        TextView mTvCurrentRoomUse = findViewById(R.id.tvCurrentRoomUse);
        TextView mTvCurrentSleep = findViewById(R.id.tvCurrentSleep);
        TextView mTvCurrentWake = findViewById(R.id.tvCurrentWake);
        ImageView mEditName = findViewById(R.id.editName);
        ImageView mEditYear = findViewById(R.id.editYear);
        ImageView mEditMajor = findViewById(R.id.editMajor);
        ImageView mEditCleanliness = findViewById(R.id.editCleanliness);
        ImageView mEditInterests = findViewById(R.id.editInterests);
        ImageView mEditActivities = findViewById(R.id.editActivities);
        ImageView mEditSmoking = findViewById(R.id.editSmoking);
        ImageView mEditDrinking = findViewById(R.id.editDrinking);
        ImageView mEditRoomUse = findViewById(R.id.editRoomUse);
        ImageView mEditSleep = findViewById(R.id.editSleep);
        ImageView mEditWake = findViewById(R.id.editWake);
        ImageView mEditBio = findViewById(R.id.editBio);
        mSwitchCurrentProfile = findViewById(R.id.switchCurrentProfile);
        Button mBtnDone = findViewById(R.id.btnDone);
        Button mBtnEditImages = findViewById(R.id.btnEditImages);

        mSwitchCurrentProfile.setOnCheckedChangeListener((buttonView, isChecked) -> mSwitchState = mSwitchCurrentProfile.isChecked());

        JSONObject mMetadata;
        mMetadata = mUser.getMetadata();
        try {
            mSwitchCurrentProfile.setChecked(mMetadata.getBoolean("ifSwitched"));
            JSONArray mRoommateArray = mMetadata.getJSONArray("roommate_profile");
            mRoommateData = mRoommateArray.getJSONObject(0);
            mTvCurrentActivity1.setText(mRoommateData.getString("activity1"));
            mTvCurrentActivity1.setBackgroundResource(R.drawable.card);
            mTvCurrentActivity2.setText(mRoommateData.getString("activity2"));
            mTvCurrentActivity2.setBackgroundResource(R.drawable.card);
            mTvCurrentActivity3.setText(mRoommateData.getString("activity3"));
            mTvCurrentActivity3.setBackgroundResource(R.drawable.card);
            mTvCurrentInterest1.setText(mRoommateData.getString("interest1"));
            mTvCurrentInterest1.setBackgroundResource(R.drawable.card);
            mTvCurrentInterest2.setText(mRoommateData.getString("interest2"));
            mTvCurrentInterest2.setBackgroundResource(R.drawable.card);
            mTvCurrentInterest3.setText(mRoommateData.getString("interest3"));
            mTvCurrentInterest3.setBackgroundResource(R.drawable.card);
            mTvCurrentName.setText(mMetadata.getString("name"));
            mTvCurrentMajor.setText(mMetadata.getString("major"));
            mTvCurrentYear.setText(mMetadata.getString("year"));
            mTvCurrentCleanliness.setText(mRoommateData.getString("cleanliness"));
            mTvCurrentSmoking.setText(mRoommateData.getString("if_smoke"));
            mTvCurrentDrinking.setText(mRoommateData.getString("if_drink"));
            if (mRoommateData.getString("room_use").equals("All of the Above")) {
                mTvCurrentRoomUse.setText("Any Use");
            } else {
                mTvCurrentRoomUse.setText(mRoommateData.getString("room_use"));
            }
            mTvCurrentSleep.setText(mRoommateData.getString("time_sleep"));
            mTvCurrentWake.setText(mRoommateData.getString("time_wake"));
            mTvCurrentBio.setText(mRoommateData.getString("bio"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mEditBio.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), BioActivity.class);
            startActivity(intent);
            finish();
        });

        mEditActivities.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), ActivitiesActivity.class);
            startActivity(intent);
            finish();
        });

        mEditInterests.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), InterestsActivity.class);
            startActivity(intent);
            finish();
        });

        mBtnDone.setOnClickListener(v -> {
            try {
                mRoommateData.put("ifSwitched", mSwitchState);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUser(mUser);
            finish();
        });

        mEditName.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), NameActivity.class);
            startActivity(intent);
            finish();
        });

        mEditYear.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), YearActivity.class);
            startActivity(intent);
            finish();
        });

        mEditMajor.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), MajorActivity.class);
            startActivity(intent);
            finish();
        });

        mEditCleanliness.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), CleanlinessActivity.class);
            startActivity(intent);
            finish();
        });

        mEditSmoking.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), SmokingActivity.class);
            startActivity(intent);
            finish();
        });

        mEditDrinking.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), DrinkingActivity.class);
            startActivity(intent);
            finish();
        });

        mEditRoomUse.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), RoomUseActivity.class);
            startActivity(intent);
            finish();
        });

        mEditSleep.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), SleepActivity.class);
            startActivity(intent);
            finish();
        });

        mEditWake.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), WakeActivity.class);
            startActivity(intent);
            finish();
        });

        mBtnEditImages.setOnClickListener(v -> {
            Intent intent = new Intent(getBaseContext(), ImagesActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void queryImages() {
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        query.include(Image.KEY_USER);
        query.whereEqualTo(Image.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting images", e);
                return;
            }
            mImages.addAll(objects);
            int mIndex = mImages.size() - 1;
            Context mContext1 = mIvCurrentImage1.getContext();
            ParseFile mImage1file = mImages.get(mIndex).getImage1Url();
            Glide.with(mContext1).load(mImage1file.getUrl()).placeholder(R.drawable.greyimage).into(mIvCurrentImage1);
            Context mContext2 = mIvCurrentImage2.getContext();
            ParseFile mImage2file = (mImages.get(mIndex)).getImage2Url();
            Glide.with(mContext2).load(mImage2file.getUrl()).placeholder(R.drawable.greyimage).into(mIvCurrentImage2);
            Context mContext3 = mIvCurrentImage3.getContext();
            ParseFile mImage3file = (mImages.get(mIndex)).getImage3Url();
            Glide.with(mContext3).load(mImage3file.getUrl()).placeholder(R.drawable.greyimage).into(mIvCurrentImage3);
            Context mContextP = mIvCurrentProfileImage.getContext();
            ParseFile mProfileImageFile = mImages.get(mIndex).getProfileImageUrl();
            Glide.with(mContextP).load(mProfileImageFile.getUrl()).placeholder(R.drawable.greyimage).circleCrop().into(mIvCurrentProfileImage);
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
}