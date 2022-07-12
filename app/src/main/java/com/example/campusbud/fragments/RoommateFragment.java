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

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.example.campusbud.CardAdapter;
import com.example.campusbud.Image;
import com.example.campusbud.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import java.util.ArrayList;
import java.util.List;

public class RoommateFragment extends Fragment {

    public Koloda koloda;
    public CardAdapter cardAdapter;
    public List<Image> allImages;
    public User currentUser;
    public int position;

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
        koloda = view.findViewById(R.id.koloda);
        allUsers = new ArrayList<>();
        allImages = new ArrayList<>();
        queryUsers();
        queryImages();
        cardAdapter = new CardAdapter(getContext(), koloda, allUsers, allImages);
        koloda.setAdapter(cardAdapter);
        Log.d(TAG, "profiles: " + allUsers);

        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {

            }

            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {

            }

            @Override
            public void onCardSwipedLeft(int i) {
                position += 1;
            }

            @Override
            public void onCardSwipedRight(int i) {
                currentUser = allUsers.get(position);
                startUserIntent(currentUser);
                position += 1;
            }

            @Override
            public void onClickRight(int i) {

            }

            @Override
            public void onClickLeft(int i) {

            }

            @Override
            public void onCardSingleTap(int i) {

            }

            @Override
            public void onCardDoubleTap(int i) {

            }

            @Override
            public void onCardLongPress(int i) {

            }

            @Override
            public void onEmptyDeck() {

            }
        });
    }

    private void startUserIntent(User user) {
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid());
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(UIKitConstants.IntentStrings.NAME, user.getName());
        intent.putExtra(UIKitConstants.IntentStrings.LINK,user.getLink());
        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
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