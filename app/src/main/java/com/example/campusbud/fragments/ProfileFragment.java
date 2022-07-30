package com.example.campusbud.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.campusbud.ProfileSettings;
import com.example.campusbud.R;
import com.example.campusbud.models.Image;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";

    private Context mContext1;
    private Context mContext2;
    private Context mContext3;
    private Context mContextP;

    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;
    private ImageView mIvPictureDisplay;

    private List<Image> mImages;

    private JSONObject mMetadata;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User mUser = CometChat.getLoggedInUser();
        mImages = new ArrayList<>();
        queryImages();

        if (mUser != null) {
            Log.d(TAG, "user is not null");
            mMetadata = mUser.getMetadata();
        } else {
            Log.d(TAG, "User is null");
        }

        if (mMetadata != null) {
            Log.d(TAG, "metadata is not null");
        } else {
            Log.d(TAG, "metadata is null");
        }

        mIvPictureDisplay = view.findViewById(R.id.ivCurrentProfileImage);
        mIvImage1 = view.findViewById(R.id.ivCurrentImage1);
        mIvImage2 = view.findViewById(R.id.ivCurrentImage2);
        mIvImage3 = view.findViewById(R.id.ivCurrentImage3);
        TextView mTvUserDisplay = view.findViewById(R.id.tvCurrentName);
        TextView mTvYearDisplay = view.findViewById(R.id.tvCurrentYear);
        TextView mTvMajorDisplay = view.findViewById(R.id.tvCurrentMajor);
        ImageView mIvSettings = view.findViewById(R.id.ivSettings);
        TextView mTvCleanlinessInput = view.findViewById(R.id.tvCurrentSmoking);
        TextView mTvSmokingInput = view.findViewById(R.id.tvCurrentCleanliness);
        TextView mTvDrinkingInput = view.findViewById(R.id.tvCurrentDrinking);
        TextView mTvRoomUseInput = view.findViewById(R.id.tvCurrentRoomUse);
        TextView mTvTimeSleepInput = view.findViewById(R.id.tvCurrentSleep);
        TextView mTvTimeWakeInput = view.findViewById(R.id.tvCurrentWake);
        TextView mTvInterests1Input = view.findViewById(R.id.tvCurrentInterest1);
        mTvInterests1Input.setBackgroundResource(R.drawable.card);
        TextView mTvInterests2Input = view.findViewById(R.id.tvInterests2Input);
        mTvInterests2Input.setBackgroundResource(R.drawable.card);
        TextView mTvInterests3Input = view.findViewById(R.id.tvInterests3Input);
        mTvInterests3Input.setBackgroundResource(R.drawable.card);
        TextView mTvActivities1Input = view.findViewById(R.id.tvActivities1Input);
        mTvActivities1Input.setBackgroundResource(R.drawable.card);
        TextView mTvActivities2Input = view.findViewById(R.id.tvActivities2Input);
        mTvActivities2Input.setBackgroundResource(R.drawable.card);
        TextView mTvActivities3Input = view.findViewById(R.id.tvActivities3Input);
        mTvActivities3Input.setBackgroundResource(R.drawable.card);
        TextView mTvBioInput = view.findViewById(R.id.tvCurrentBio);

        if (mMetadata != null) {
            try {
                mTvUserDisplay.setText(mMetadata.getString("name"));
                mTvYearDisplay.setText(mMetadata.getString("year"));
                mTvMajorDisplay.setText(mMetadata.getString("major"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mMetadata != null) {
            Log.d(TAG, "metadata:" + mMetadata);
            if (mMetadata.has("roommate_profile")) {
                try {
                    JSONArray roommateProfileArray = mMetadata.getJSONArray("roommate_profile");
                    JSONObject roommateProfile = roommateProfileArray.getJSONObject(0);
                    mTvCleanlinessInput.setText(roommateProfile.getString("cleanliness"));
                    mTvSmokingInput.setText(roommateProfile.getString("if_smoke"));
                    mTvDrinkingInput.setText(roommateProfile.getString("if_drink"));
                    if (roommateProfile.getString("room_use").equals("All of the Above")) {
                        mTvRoomUseInput.setText("Any Use");
                    } else {
                        mTvRoomUseInput.setText(roommateProfile.getString("room_use"));
                    }
                    mTvTimeSleepInput.setText(roommateProfile.getString("time_sleep"));
                    mTvTimeWakeInput.setText(roommateProfile.getString("time_wake"));
                    mTvInterests1Input.setText(roommateProfile.getString("interest1"));
                    mTvInterests2Input.setText(roommateProfile.getString("interest2"));
                    mTvInterests3Input.setText(roommateProfile.getString("interest3"));
                    mTvActivities1Input.setText(roommateProfile.getString("activity1"));
                    mTvActivities2Input.setText(roommateProfile.getString("activity2"));
                    mTvActivities3Input.setText(roommateProfile.getString("activity3"));
                    mTvBioInput.setText(roommateProfile.getString("bio"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mIvSettings.setOnClickListener(v -> launchSettings());
    }

    private void launchSettings() {
        Intent intent = new Intent(getActivity(), ProfileSettings.class);
        startActivity(intent);
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
            if(mImages.size() > 0) {
                int mIndex = mImages.size() - 1;
                mContext1 = mIvImage1.getContext();
                ParseFile mImage1file = mImages.get(mIndex).getImage1Url();
                Glide.with(mContext1).load(mImage1file.getUrl()).placeholder(R.drawable.greyimage).into(mIvImage1);
                mContext2 = mIvImage2.getContext();
                ParseFile mImage2file = (mImages.get(mIndex)).getImage2Url();
                Glide.with(mContext2).load(mImage2file.getUrl()).placeholder(R.drawable.greyimage).into(mIvImage2);
                mContext3 = mIvImage3.getContext();
                ParseFile mImage3file = (mImages.get(mIndex)).getImage3Url();
                Glide.with(mContext3).load(mImage3file.getUrl()).placeholder(R.drawable.greyimage).into(mIvImage3);
                mContextP = mIvPictureDisplay.getContext();
                ParseFile mProfileImageFile = mImages.get(mIndex).getProfileImageUrl();
                Glide.with(mContextP).load(mProfileImageFile.getUrl()).placeholder(R.drawable.greyimage).circleCrop().into(mIvPictureDisplay);
            }
        });
    }

}