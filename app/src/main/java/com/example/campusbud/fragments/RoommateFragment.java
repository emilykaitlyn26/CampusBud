package com.example.campusbud.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.CardAdapter;
import com.example.campusbud.Image;
import com.example.campusbud.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.yalantis.library.Koloda;

import java.util.ArrayList;
import java.util.List;

public class RoommateFragment extends Fragment {

    public Koloda adapter;
    public CardAdapter cardAdapter;
    public List<Image> allImages;

    private final String TAG = "RoommateFragment";

    public List<User> allUsers;

    public RoommateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roommate, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = view.findViewById(R.id.koloda);
        allUsers = new ArrayList<>();
        allImages = new ArrayList<>();
        queryUsers();
        queryImages();
        cardAdapter = new CardAdapter(getContext(), allUsers, allImages);
        adapter.setAdapter(cardAdapter);
        Log.d(TAG, "profiles: " + allUsers);
    }

    public void queryUsers() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers.addAll(users);
                cardAdapter.notifyDataSetChanged();
                Log.d(TAG, "User list received: " + users.size());
                Log.d(TAG, "Users" + users);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    public void queryImages() {
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        //query.whereContains("User", userProfile.getUid());
        query.findInBackground(new FindCallback<Image>() {
            @Override
            public void done(List<Image> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting images", e);
                    return;
                }
                allImages.addAll(objects);
                cardAdapter.notifyDataSetChanged();
            }
        });
    }
}