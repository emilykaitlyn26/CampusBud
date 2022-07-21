package com.example.campusbud.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.campusbud.Image;
import com.example.campusbud.ProfileSettings;
import com.example.campusbud.R;
import com.parse.FindCallback;
import com.parse.ParseException;
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

    private List<Image> mImages;

    private JSONObject mMetadata;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

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

        ImageView mIvPictureDisplay = view.findViewById(R.id.ivPictureDisplay);
        ImageView mIvImage1 = view.findViewById(R.id.ivImage1);
        ImageView mIvImage2 = view.findViewById(R.id.ivImage2);
        ImageView mIvImage3 = view.findViewById(R.id.ivImage3);
        TextView mTvUserDisplay = view.findViewById(R.id.tvUserDisplay);
        TextView mTvYearDisplay = view.findViewById(R.id.tvYearDisplay);
        TextView mTvMajorDisplay = view.findViewById(R.id.tvMajorDisplay);
        ImageView mIvSettings = view.findViewById(R.id.ivSettings);
        TextView mTvCleanlinessInput = view.findViewById(R.id.tvCleanlinessInput);
        TextView mTvSmokingInput = view.findViewById(R.id.tvSmokingInput);
        TextView mTvDrinkingInput = view.findViewById(R.id.tvDrinkingInput);
        TextView mTvRoomUseInput = view.findViewById(R.id.tvRoomUseInput);
        TextView mTvTimeSleepInput = view.findViewById(R.id.tvTimeSleepInput);
        TextView mTvTimeWakeInput = view.findViewById(R.id.tvTimeWakeInput);
        TextView mTvInterestsInput = view.findViewById(R.id.tvInterestsInput);
        TextView mTvActivitiesInput = view.findViewById(R.id.tvActivitiesInput);
        TextView mTvBioInput = view.findViewById(R.id.tvBioInput);
        Button mBtnRefresh = view.findViewById(R.id.btnRefresh);

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
                    mTvRoomUseInput.setText(roommateProfile.getString("room_use"));
                    mTvTimeSleepInput.setText(roommateProfile.getString("time_sleep"));
                    mTvTimeWakeInput.setText(roommateProfile.getString("time_wake"));
                    mTvInterestsInput.setText(roommateProfile.getString("interests"));
                    mTvActivitiesInput.setText(roommateProfile.getString("activities"));
                    mTvBioInput.setText(roommateProfile.getString("bio"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mBtnRefresh.setOnClickListener(v -> {
             if (mImages.size() > 0) {
                int index = mImages.size() - 1;
                mContext1 = mIvImage1.getContext();
                ParseFile image1file = mImages.get(index).getImage1Url();
                Glide.with(mContext1).load(image1file.getUrl()).into(mIvImage1);
                mContext2 = mIvImage2.getContext();
                ParseFile image2file = (mImages.get(index)).getImage2Url();
                Glide.with(mContext2).load(image2file.getUrl()).into(mIvImage2);
                mContext3 = mIvImage3.getContext();
                ParseFile image3file = (mImages.get(index)).getImage3Url();
                Glide.with(mContext3).load(image3file.getUrl()).into(mIvImage3);
                mContextP = mIvPictureDisplay.getContext();
                ParseFile profileImageFile = mImages.get(index).getProfileImageUrl();
                Glide.with(mContextP).load(profileImageFile.getUrl()).circleCrop().into(mIvPictureDisplay);
            }
        });

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
        });
    }

}