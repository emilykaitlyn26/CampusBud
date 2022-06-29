package com.example.campusbud.fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.ProfileSettings;
import com.example.campusbud.R;
import com.example.campusbud.models.Profile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    Profile profile;
    private final String TAG = "ProfileFragment";

    ParseUser parseUser;
    User user;
    JSONObject metadata;

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

        try {
            profile = Profile.fromJson(metadata);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView ivPictureDisplay;
        TextView tvUserDisplay;
        TextView tvYearDisplay;
        TextView tvMajorDisplay;
        ImageView ivSettings;

        ivPictureDisplay = view.findViewById(R.id.ivPictureDisplay);
        tvUserDisplay = view.findViewById(R.id.tvUserDisplay);
        tvYearDisplay = view.findViewById(R.id.tvYearDisplay);
        tvMajorDisplay = view.findViewById(R.id.tvMajorDisplay);
        ivSettings = view.findViewById(R.id.ivSettings);

        tvUserDisplay.setText(profile.name);
        tvYearDisplay.setText(profile.year);
        tvMajorDisplay.setText(profile.major);

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