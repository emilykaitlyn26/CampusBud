package com.example.campusbud.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;
import com.example.campusbud.ProfileSettings;
import com.example.campusbud.R;
import com.example.campusbud.models.Profile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    private final String TAG = "ProfileFragment";

    public Profile profile;

    ParseUser parseUser;
    User user;
    JSONObject metadata;
    public JSONObject roommateProfile = new JSONObject();
    public JSONArray roommateProfileArray = new JSONArray();

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parseUser = ParseUser.getCurrentUser();
        user = CometChat.getLoggedInUser();
        Log.e(TAG, "User is " + parseUser.toString() + " parse and " + user + " comet");

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

        try {
            profile = Profile.fromJson(metadata);
            Log.e(TAG, "profile is not null");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "profile is null");
        }

        ImageView ivPictureDisplay;
        TextView tvUserDisplay;
        TextView tvYearDisplay;
        TextView tvMajorDisplay;
        ImageView ivSettings;
        TextView tvRoomCleanliness;
        TextView tvCleanlinessInput;
        TextView tvSmoking;
        TextView tvDrinking;
        TextView tvSmokingInput;
        TextView tvDrinkingInput;
        TextView tvRoomUseDisplay;
        TextView tvRoomUseInput;

        ivPictureDisplay = view.findViewById(R.id.ivPictureDisplay);
        tvUserDisplay = view.findViewById(R.id.tvUserDisplay);
        tvYearDisplay = view.findViewById(R.id.tvYearDisplay);
        tvMajorDisplay = view.findViewById(R.id.tvMajorDisplay);
        ivSettings = view.findViewById(R.id.ivSettings);
        tvRoomCleanliness = view.findViewById(R.id.tvRoomCleanliness);
        tvCleanlinessInput = view.findViewById(R.id.tvCleanlinessInput);
        tvSmoking = view.findViewById(R.id.tvSmoking);
        tvDrinking = view.findViewById(R.id.tvDrinking);
        tvSmokingInput = view.findViewById(R.id.tvSmokingInput);
        tvDrinkingInput = view.findViewById(R.id.tvDrinkingInput);
        tvRoomUseDisplay = view.findViewById(R.id.tvTimeSleep);
        tvRoomUseInput = view.findViewById(R.id.tvRoomUseInput);

        try {
            tvUserDisplay.setText(metadata.getString("name"));
            tvYearDisplay.setText(metadata.getString("year"));
            tvMajorDisplay.setText(metadata.getString("major"));
        } catch (JSONException e) {
            e.printStackTrace();
        }//);
        //
        //;

        Log.d(TAG, "metadata:" + metadata);
         if (metadata.has("roommate_profile")) {
             try {
                 roommateProfileArray = metadata.getJSONArray("roommate_profile");
                 roommateProfile = roommateProfileArray.getJSONObject(0);
                 tvCleanlinessInput.setText(roommateProfile.getString("cleanliness"));
                 tvSmokingInput.setText(roommateProfile.getString("if_smoke"));
                 tvDrinkingInput.setText(roommateProfile.getString("if_drink"));
                 tvRoomUseInput.setText(roommateProfile.getString("room_use"));
             } catch (JSONException e) {
                 e.printStackTrace();
             }
            tvRoomCleanliness.setVisibility(View.VISIBLE);
            tvCleanlinessInput.setVisibility(View.VISIBLE);
            tvSmoking.setVisibility(View.VISIBLE);
            tvDrinking.setVisibility(View.VISIBLE);
            tvSmokingInput.setVisibility(View.VISIBLE);
            tvDrinkingInput.setVisibility(View.VISIBLE);
            tvRoomUseDisplay.setVisibility(View.VISIBLE);
            tvRoomUseInput.setVisibility(View.VISIBLE);
        } else {
            tvRoomCleanliness.setVisibility(View.GONE);
            tvCleanlinessInput.setVisibility(View.GONE);
            tvSmoking.setVisibility(View.GONE);
            tvDrinking.setVisibility(View.GONE);
            tvSmokingInput.setVisibility(View.GONE);
            tvDrinkingInput.setVisibility(View.GONE);
        }

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

}