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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoommateFragment extends Fragment {

    public Koloda koloda;
    public CardAdapter cardAdapter;
    public List<Image> allImages;
    public User currentUser;
    public int position;
    public List<User> getUser;

    public double max = 0.7;
    public double average = 0.5;
    public double below = 0.3;
    public double lowest = 0.1;

    public int userYearValue;
    public int loggedInYearValue;
    public int userCleanlinessValue;
    public int loggedInCleanlinessValue;
    public int userSmokingValue;
    public int loggedInSmokingValue;
    public int userDrinkingValue;
    public int loggedInDrinkingValue;

    public double rating; // total rating score tbd
    public double year;
    public double cleanliness;
    public double drinking;
    public double smoking;
    public double roomUse;
    public double timeSleep;
    public double timeWake;

    private final String TAG = "RoommateFragment";

    public List<User> allUsers;
    public List<User> sortedUsers;
    List<Double> ratings;

    public RoommateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roommate, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        koloda = view.findViewById(R.id.koloda);
        getUser = new ArrayList<>();
        allUsers = new ArrayList<>();
        ratings = new ArrayList<>();
        sortedUsers = new ArrayList<>();
        allImages = new ArrayList<>();
        queryProfiles();
        queryUsers();
        queryImages();
        cardAdapter = new CardAdapter(getContext(), koloda, sortedUsers, allImages);
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
                currentUser = getUser.get(position);
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
                User loggedInUser = CometChat.getLoggedInUser();
                JSONObject currentUserMetadata = loggedInUser.getMetadata();
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    JSONObject userMetadata = user.getMetadata();
                    boolean switched = false;
                    try {
                        switched = userMetadata.getBoolean("ifSwitched");
                        if (switched) {
                            String userCollege = userMetadata.getString("college");
                            String currentUserCollege = currentUserMetadata.getString("college");
                            if (userCollege.equals(currentUserCollege)) {
                                String userGender = userMetadata.getString("gender");
                                String currentUserGender = currentUserMetadata.getString("gender");
                                if (userGender.equals(currentUserGender)) {
                                    allUsers.add(user);
                                    double userRate  = rate(user);
                                    ratings.add(userRate);
                                }
                            }
                        }
                    } catch (JSONException | java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < ratings.size(); i++) {
                    double maxValue = Collections.max(ratings);
                    int maxIndex = ratings.indexOf(maxValue);
                    sortedUsers.add(allUsers.get(maxIndex));
                    ratings.set(maxIndex, 0.0);
                }
                //Collections.reverse(sortedUsers);
                cardAdapter.notifyDataSetChanged();
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

    protected void queryProfiles() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                getUser = users;
                Log.d(TAG, "User list received: " + users.size());
                Log.d(TAG, "Users" + users);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    public void matching(List<User> allUsers) {

    }

    public double rate(User user) throws JSONException, java.text.ParseException {
        currentUser = CometChat.getLoggedInUser();
        JSONObject loggedInUserData = currentUser.getMetadata();
        JSONObject userData = user.getMetadata();

        String userYear = userData.getString("year");
        String loggedInYear = loggedInUserData.getString("year");

        if (userYear.equals("Senior")) {
            userYearValue = 4;
        } else if (userYear.equals("Junior")) {
            userYearValue = 3;
        } else if (userYear.equals("Sophomore")) {
            userYearValue = 2;
        } else if (userYear.equals("Freshman")) {
            userYearValue = 1;
        }

        if (loggedInYear.equals("Senior")) {
            loggedInYearValue = 4;
        } else if (loggedInYear.equals("Junior")) {
            loggedInYearValue = 3;
        } else if (loggedInYear.equals("Sophomore")) {
            loggedInYearValue = 2;
        } else if (loggedInYear.equals("Freshman")) {
            loggedInYearValue = 1;
        }

        int yearDifference = Math.abs(userYearValue - loggedInYearValue);

        if (yearDifference == 0) {
            year = max;
        } else if (yearDifference == 1) {
            year = average;
        } else if (yearDifference == 2) {
            year = below;
        } else if (yearDifference == 3) {
            year = lowest;
        }

        JSONArray userRoommateProfile = userData.getJSONArray("roommate_profile");
        JSONArray loggedInRoommateProfile = loggedInUserData.getJSONArray("roommate_profile");
        JSONObject userRoommateData = userRoommateProfile.getJSONObject(0);
        JSONObject loggedInRoommateData = loggedInRoommateProfile.getJSONObject(0);
        String userCleanliness = userRoommateData.getString("cleanliness");
        String loggedInCleanliness = loggedInRoommateData.getString("cleanliness");

        if (userCleanliness.equals("Organized")) {
            userCleanlinessValue = 4;
        } else if (userCleanliness.equals("Casual")) {
            userCleanlinessValue = 3;
        } else if (userCleanliness.equals("Occasionally messy")) {
            userCleanlinessValue = 2;
        } else if (userCleanliness.equals("Messy")) {
            userCleanlinessValue = 1;
        }

        if (loggedInCleanliness.equals("Organized")) {
            loggedInCleanlinessValue = 4;
        } else if (loggedInCleanliness.equals("Casual")) {
            loggedInCleanlinessValue = 3;
        } else if (loggedInCleanliness.equals("Occasionally messy")) {
            loggedInCleanlinessValue = 2;
        } else if (loggedInCleanliness.equals("Messy")) {
            loggedInCleanlinessValue = 1;
        }

        int cleanlinessDifference = Math.abs(userCleanlinessValue - loggedInCleanlinessValue);

        if (cleanlinessDifference == 0) {
            cleanliness = max;
        } else if (cleanlinessDifference == 1) {
            cleanliness = average;
        } else if (cleanlinessDifference == 2) {
            cleanliness = below;
        } else if (cleanlinessDifference == 3) {
            cleanliness = lowest;
        }

        String userSmoking = userRoommateData.getString("if_smoke");
        String loggedInSmoking = loggedInRoommateData.getString("if_smoke");

        if (userSmoking.equals("Yes")) {
            userSmokingValue = 3;
        } else if (userSmoking.equals("Sometimes")) {
            userSmokingValue = 2;
        } else if (userSmoking.equals("No")) {
            userSmokingValue = 1;
        }

        if (loggedInSmoking.equals("Yes")) {
            loggedInSmokingValue = 3;
        } else if (loggedInSmoking.equals("Sometimes")) {
            loggedInSmokingValue = 2;
        } else if (loggedInSmoking.equals("No")) {
            loggedInSmokingValue = 1;
        }

        int smokingDifference = Math.abs(userSmokingValue - loggedInSmokingValue);

        if (smokingDifference == 0) {
            smoking = max;
        } else if (smokingDifference == 1) {
            smoking = average;
        } else if (smokingDifference == 2) {
            smoking = below;
        }

        String userDrinking = userRoommateData.getString("if_drink");
        String loggedInDrinking = loggedInRoommateData.getString("if_drink");

        if (userDrinking.equals("Yes")) {
            userDrinkingValue = 3;
        } else if (userDrinking.equals("Sometimes")) {
            userDrinkingValue = 2;
        } else if (userDrinking.equals("No")) {
            userDrinkingValue = 1;
        }

        if (loggedInDrinking.equals("Yes")) {
            loggedInDrinkingValue = 3;
        } else if (loggedInDrinking.equals("Sometimes")) {
            loggedInDrinkingValue = 2;
        } else if (loggedInDrinking.equals("No")) {
            loggedInDrinkingValue = 1;
        }

        int drinkingDifference = Math.abs(userDrinkingValue - loggedInDrinkingValue);

        if (drinkingDifference == 0) {
            drinking = max;
        } else if (drinkingDifference == 1) {
            drinking = average;
        } else if (drinkingDifference == 2) {
            drinking = below;
        }

        String userRoomUse = userRoommateData.getString("room_use");
        String loggedInRoomUse = loggedInRoommateData.getString("room_use");

        if (userRoomUse.equals(loggedInRoomUse)) {
            roomUse = max;
        } else {
            roomUse = average;
        }

        String userTimeSleepString = userRoommateData.getString("time_sleep");
        String loggedInTimeSleepString = loggedInRoommateData.getString("time_sleep");
        DateFormat dateFormat = new SimpleDateFormat("hh:mmaa");
        Date userTimeSleep = dateFormat.parse(userTimeSleepString);
        Date loggedInTimeSleep = dateFormat.parse(loggedInTimeSleepString);
        long sleepTimeDifference = Math.abs((userTimeSleep.getTime() - loggedInTimeSleep.getTime()) / (60 * 60 * 1000) % 24);

        if (sleepTimeDifference == 0) {
            timeSleep = max;
        } else if (sleepTimeDifference == 1 || sleepTimeDifference == 2) {
            timeSleep = average;
        } else if (sleepTimeDifference <= 5) {
            timeSleep = below;
        } else {
            timeSleep = lowest;
        }

        String userTimeWakeString = userRoommateData.getString("time_wake");
        String loggedInTimeWakeString = loggedInRoommateData.getString("time_wake");
        Date userTimeWake = dateFormat.parse(userTimeWakeString);
        Date loggedInTimeWake = dateFormat.parse(loggedInTimeWakeString);
        long wakeTimeDifference = Math.abs((userTimeWake.getTime() - loggedInTimeWake.getTime()) / (60 * 60 * 1000) % 24);

        if (wakeTimeDifference == 0) {
            timeWake = max;
        } else if (wakeTimeDifference == 1 || wakeTimeDifference == 2) {
            timeWake = average;
        } else if (wakeTimeDifference <= 5) {
            timeWake = below;
        } else {
            timeWake = lowest;
        }

        rating = year + cleanliness + smoking + drinking + roomUse + timeSleep + timeWake;
        return rating;
    }

    public void updateUser(User user) {
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