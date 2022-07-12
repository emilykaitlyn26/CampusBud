package com.example.campusbud.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Base64;
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
import com.example.campusbud.models.Profile;
import com.parse.DeleteCallback;
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
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";

    public Profile profile;
    public Context context1;
    public Context context2;
    public Context context3;
    public Context contextP;
    public List<Image> images;

    ParseUser parseUser;
    User user;
    JSONObject metadata;
    public JSONObject roommateProfile = new JSONObject();
    public JSONArray roommateProfileArray = new JSONArray();

    ImageView ivPictureDisplay;
    ImageView ivImage1;
    ImageView ivImage2;
    ImageView ivImage3;
    TextView tvUserDisplay;
    TextView tvYearDisplay;
    TextView tvMajorDisplay;
    ImageView ivSettings;
    TextView tvCleanlinessInput;
    TextView tvSmokingInput;
    TextView tvDrinkingInput;
    TextView tvRoomUseInput;
    TextView tvTimeSleepInput;
    TextView tvTimeWakeInput;
    TextView tvInterestsInput;
    TextView tvActivitiesInput;
    TextView tvBioInput;
    Button btnRefresh;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseUser = ParseUser.getCurrentUser();
        user = CometChat.getLoggedInUser();
        images = new ArrayList<>();
        queryImages();

        if (user != null) {
            Log.d(TAG, "user is not null");
            metadata = user.getMetadata();
        } else {
            Log.d(TAG, "User is null");
        }

        if (metadata != null) {
            Log.d(TAG, "metadata is not null");
        } else {
            Log.d(TAG, "metadata is null");
        }

        ivPictureDisplay = view.findViewById(R.id.ivPictureDisplay);
        ivImage1 = view.findViewById(R.id.ivImage1);
        ivImage2 = view.findViewById(R.id.ivImage2);
        ivImage3 = view.findViewById(R.id.ivImage3);
        tvUserDisplay = view.findViewById(R.id.tvUserDisplay);
        tvYearDisplay = view.findViewById(R.id.tvYearDisplay);
        tvMajorDisplay = view.findViewById(R.id.tvMajorDisplay);
        ivSettings = view.findViewById(R.id.ivSettings);
        tvCleanlinessInput = view.findViewById(R.id.tvCleanlinessInput);
        tvSmokingInput = view.findViewById(R.id.tvSmokingInput);
        tvDrinkingInput = view.findViewById(R.id.tvDrinkingInput);
        tvRoomUseInput = view.findViewById(R.id.tvRoomUseInput);
        tvTimeSleepInput = view.findViewById(R.id.tvTimeSleepInput);
        tvTimeWakeInput = view.findViewById(R.id.tvTimeWakeInput);
        tvInterestsInput = view.findViewById(R.id.tvInterestsInput);
        tvActivitiesInput = view.findViewById(R.id.tvActivitiesInput);
        tvBioInput = view.findViewById(R.id.tvBioInput);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        if (metadata != null) {
            try {
                tvUserDisplay.setText(metadata.getString("name"));
                tvYearDisplay.setText(metadata.getString("year"));
                tvMajorDisplay.setText(metadata.getString("major"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (metadata != null) {
            Log.d(TAG, "metadata:" + metadata);
            if (metadata.has("roommate_profile")) {
                try {
                    roommateProfileArray = metadata.getJSONArray("roommate_profile");
                    roommateProfile = roommateProfileArray.getJSONObject(0);
                    tvCleanlinessInput.setText(roommateProfile.getString("cleanliness"));
                    tvSmokingInput.setText(roommateProfile.getString("if_smoke"));
                    tvDrinkingInput.setText(roommateProfile.getString("if_drink"));
                    tvRoomUseInput.setText(roommateProfile.getString("room_use"));
                    tvTimeSleepInput.setText(roommateProfile.getString("time_sleep"));
                    tvTimeWakeInput.setText(roommateProfile.getString("time_wake"));
                    tvInterestsInput.setText(roommateProfile.getString("interests"));
                    tvActivitiesInput.setText(roommateProfile.getString("activities"));
                    tvBioInput.setText(roommateProfile.getString("bio"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (images.size() > 0) {
                    int index = images.size() - 1;
                    context1 = ivImage1.getContext();
                    ParseFile image1file = images.get(index).getImage1Url();
                    Glide.with(context1).load(image1file.getUrl()).into(ivImage1);
                    context2 = ivImage2.getContext();
                    ParseFile image2file = (images.get(index)).getImage2Url();
                    Glide.with(context2).load(image2file.getUrl()).into(ivImage2);
                    context3 = ivImage3.getContext();
                    ParseFile image3file = (images.get(index)).getImage3Url();
                    Glide.with(context3).load(image3file.getUrl()).into(ivImage3);
                    contextP = ivPictureDisplay.getContext();
                    ParseFile profileImageFile = images.get(index).getProfileImageUrl();
                    Glide.with(contextP).load(profileImageFile.getUrl()).circleCrop().into(ivPictureDisplay);
                }
            }
        });

        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSettings();
            }
        });
    }

    private void launchSettings() {
        Intent intent = new Intent(getActivity(), ProfileSettings.class);
        startActivity(intent);
    }

    public void queryImages() {
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        query.include(Image.KEY_USER);
        query.whereEqualTo(Image.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Image>() {
            @Override
            public void done(List<Image> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting images", e);
                    return;
                }
                images.addAll(objects);
            }
        });
    }

}