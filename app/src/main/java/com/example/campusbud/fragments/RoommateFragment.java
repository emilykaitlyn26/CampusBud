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

    public double max = 0.7;
    public double average = 0.5;
    public double below = 0.3;
    public double lowest = 0.1;

    private double yearMax;
    private double yearAverage;
    private double yearBelow;
    private double yearLowest;
    private double cleanMax;
    private double cleanAverage;
    private double cleanBelow;
    private double cleanLowest;
    private double smokingMax;
    private double smokingAverage;
    private double smokingBelow;
    private double drinkingMax;
    private double drinkingAverage;
    private double drinkingBelow;
    private double roomMax;
    private double roomAverage;
    private double roomBelow;
    private double roomLowest;

    public int userYearValue;
    public int loggedInYearValue;
    public int userCleanlinessValue;
    public int loggedInCleanlinessValue;
    public int userSmokingValue;
    public int loggedInSmokingValue;
    public int userDrinkingValue;
    public int loggedInDrinkingValue;

    public double rating;
    public double year;
    public double cleanliness;
    public double drinking;
    public double smoking;
    public double roomUse;
    public double timeSleep;
    public double timeWake;

    private int increment;
    private int deckIncrement = 0;

    private final String TAG = "RoommateFragment";

    public List<User> allUsers;
    public List<User> sortedUsers;
    List<Double> ratings;
    JSONObject metadata;

    private JSONArray userActivity;
    private JSONArray userYearActivity;
    private JSONObject userYearValues;
    private JSONArray userCleanlinessActivity;
    private JSONObject userCleanlinessValues;
    private JSONArray userSmokingActivity;
    private JSONObject userSmokingValues;
    private JSONArray userDrinkingActivity;
    private JSONObject userDrinkingValues;
    private JSONArray userRoomUseActivity;
    private JSONObject userRoomUseValues;

    public RoommateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roommate, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        metadata = currentUser.getMetadata();
        try {
            userActivity = metadata.getJSONArray("user_activity");
            userYearActivity = userActivity.getJSONArray(0);
            userYearValues = userYearActivity.getJSONObject(0);
            userCleanlinessActivity = userActivity.getJSONArray(1);
            userCleanlinessValues = userCleanlinessActivity.getJSONObject(0);
            userSmokingActivity = userActivity.getJSONArray(3);
            userSmokingValues = userSmokingActivity.getJSONObject(0);
            userDrinkingActivity = userActivity.getJSONArray(4);
            userDrinkingValues = userDrinkingActivity.getJSONObject(0);
            userRoomUseActivity = userActivity.getJSONArray(5);
            userRoomUseValues = userRoomUseActivity.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        koloda = view.findViewById(R.id.koloda);
        allUsers = new ArrayList<>();
        ratings = new ArrayList<>();
        sortedUsers = new ArrayList<>();
        allImages = new ArrayList<>();
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
                currentUser = sortedUsers.get(position);
                try {
                    updateUserLeft(currentUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                position += 1;
            }

            @Override
            public void onCardSwipedRight(int i) {
                currentUser = sortedUsers.get(position);
                try {
                    updateUserRight(currentUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                                    double userRate = rate(user);
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
                deckIncrement += 1;
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

    public void updateUser(User currentUser, int increment) throws JSONException {
        JSONObject currentUserData = currentUser.getMetadata();
        JSONArray currentUserRoommateProfile = currentUserData.getJSONArray("roommate_profile");
        JSONObject currentUserRoommateData = currentUserRoommateProfile.getJSONObject(0);

        String currentUserYear = currentUserData.getString("year");
        if (currentUserYear.equals("Freshman")) {
            int currYearVal = userYearValues.getInt("freshman");
            currYearVal += increment;
            userYearValues.remove("freshman");
            userYearValues.put("freshman", currYearVal);
        } else if (currentUserYear.equals("Sophomore")) {
            int currYearVal = userYearValues.getInt("sophomore");
            currYearVal += increment;
            userYearValues.remove("sophomore");
            userYearValues.put("sophomore", currYearVal);
        } else if (currentUserYear.equals("Junior")) {
            int currYearVal = userYearValues.getInt("junior");
            currYearVal += increment;
            userYearValues.remove("junior");
            userYearValues.put("junior", currYearVal);
        } else if (currentUserYear.equals("Senior")) {
            int currYearVal = userYearValues.getInt("senior");
            currYearVal += increment;
            userYearValues.remove("senior");
            userYearValues.put("senior", currYearVal);
        }

        String currentUserCleanliness = currentUserRoommateData.getString("cleanliness");
        if (currentUserCleanliness.equals("Organized")) {
            int currCleanlinessVal = userCleanlinessValues.getInt("organized");
            currCleanlinessVal += increment;
            userCleanlinessValues.remove("organized");
            userCleanlinessValues.put("organized", currCleanlinessVal);
        } else if (currentUserCleanliness.equals("Casual")) {
            int currCleanlinessVal = userCleanlinessValues.getInt("casual");
            currCleanlinessVal += increment;
            userCleanlinessValues.remove("casual");
            userCleanlinessValues.put("casual", currCleanlinessVal);
        } else if (currentUserCleanliness.equals("Occasionally messy")) {
            int currCleanlinessVal = userCleanlinessValues.getInt("occasionally_messy");
            currCleanlinessVal += increment;
            userCleanlinessValues.remove("occasionally_messy");
            userCleanlinessValues.put("occasionally_messy", currCleanlinessVal);
        } else if (currentUserCleanliness.equals("Messy")) {
            int currCleanlinessVal = userCleanlinessValues.getInt("messy");
            currCleanlinessVal += increment;
            userCleanlinessValues.remove("messy");
            userCleanlinessValues.put("messy", currCleanlinessVal);
        }

        String currentUserSmoking = currentUserRoommateData.getString("if_smoke");
        if (currentUserSmoking.equals("Yes")) {
            int currSmokingVal = userSmokingValues.getInt("yes");
            currSmokingVal += increment;
            userSmokingValues.remove("yes");
            userSmokingValues.put("yes", currSmokingVal);
        } else if (currentUserSmoking.equals("Sometimes")) {
            int currSmokingVal = userSmokingValues.getInt("sometimes");
            currSmokingVal += increment;
            userSmokingValues.remove("sometimes");
            userSmokingValues.put("sometimes", currSmokingVal);
        } else if (currentUserSmoking.equals("No")) {
            int currSmokingVal = userSmokingValues.getInt("no");
            currSmokingVal += increment;
            userSmokingValues.remove("no");
            userSmokingValues.put("no", currSmokingVal);
        }

        String currentUserDrinking = currentUserRoommateData.getString("if_drink");
        if (currentUserDrinking.equals("Yes")) {
            int currDrinkingVal = userDrinkingValues.getInt("yes");
            currDrinkingVal += increment;
            userDrinkingValues.remove("yes");
            userDrinkingValues.put("yes", currDrinkingVal);
        } else if (currentUserDrinking.equals("Sometimes")) {
            int currDrinkingVal = userDrinkingValues.getInt("sometimes");
            currDrinkingVal += increment;
            userDrinkingValues.remove("sometimes");
            userDrinkingValues.put("sometimes", currDrinkingVal);
        } else if (currentUserDrinking.equals("No")) {
            int currDrinkingVal = userDrinkingValues.getInt("no");
            currDrinkingVal += increment;
            userDrinkingValues.remove("no");
            userDrinkingValues.put("no", currDrinkingVal);
        }

        String currentUserRoomUse = currentUserRoommateData.getString("room_use");
        if (currentUserRoomUse.equals("Social Space")) {
            int currRoomUseVal = userRoomUseValues.getInt("social_space");
            currRoomUseVal += increment;
            userRoomUseValues.remove("social_space");
            userRoomUseValues.put("social_space", currRoomUseVal);
        } else if (currentUserRoomUse.equals("Study Space")) {
            int currRoomUseVal = userRoomUseValues.getInt("study_space");
            currRoomUseVal += increment;
            userRoomUseValues.remove("study_space");
            userRoomUseValues.put("study_space", currRoomUseVal);
        } else if (currentUserRoomUse.equals("Sleeping Space")) {
            int currRoomUseVal = userCleanlinessValues.getInt("sleeping_space");
            currRoomUseVal += increment;
            userRoomUseValues.remove("sleeping_space");
            userRoomUseValues.put("sleeping_space", currRoomUseVal);
        } else if (currentUserRoomUse.equals("All of the Above")) {
            int currRoomUseVal = userCleanlinessValues.getInt("all_above");
            currRoomUseVal += increment;
            userRoomUseValues.remove("all_above");
            userRoomUseValues.put("all_above", currRoomUseVal);
        }
    }

    public void updateUserLeft(User currentUser) throws JSONException {
        increment = -1;
        updateUser(currentUser, increment);
    }

    public void updateUserRight(User currentUser) throws JSONException {
        increment = 1;
        updateUser(currentUser, increment);
    }

    public void changeRatings(User currentUser) throws JSONException {
        JSONObject currentUserData = currentUser.getMetadata();
        JSONArray currentUserRates = currentUserData.getJSONArray("rate_values");
        JSONObject currentUserRateValues = currentUserRates.getJSONObject(0);

        JSONArray activity = currentUserData.getJSONArray("user_activity");
        JSONArray yearActivityArray = activity.getJSONArray(0);
        JSONObject yearActivity = yearActivityArray.getJSONObject(0);
        int freshValue = yearActivity.getInt("freshman");
        int sophValue = yearActivity.getInt("sophomore");
        int junValue = yearActivity.getInt("junior");
        int senValue = yearActivity.getInt("senior");
        List<Integer> yearArray = new ArrayList<Integer>(Arrays.asList(freshValue, sophValue, junValue, senValue));
        Integer[] tempYearArray = {freshValue, sophValue, junValue, senValue};
        int ycount = 0;
        for (int i = 0; i < yearArray.size(); i++) {
            int max = (int) Collections.max(yearArray);
            int indexOfMax = Arrays.asList(tempYearArray).indexOf(max);
            if (indexOfMax == 0) {
                if (ycount == 0) {
                    if (yearMax < 0.9) {
                        yearMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (yearAverage < (yearMax + 0.02) && yearAverage > yearBelow) {
                        yearAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (yearBelow < yearAverage && yearBelow > (yearLowest - 0.02)) {
                        yearBelow -= 0.02;
                    }
                } else if (ycount == 3) {
                    if (yearLowest < yearBelow && yearLowest > 0.5) {
                        yearLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 1) {
                if (ycount == 0) {
                    if (yearMax < 0.9) {
                        yearMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (yearAverage < (yearMax + 0.02) && yearAverage > yearBelow) {
                        yearAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (yearBelow < yearAverage && yearBelow > (yearLowest - 0.02)) {
                        yearBelow -= 0.02;
                    }
                } else if (ycount == 3) {
                    if (yearLowest < yearBelow && yearLowest > 0.5) {
                        yearLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 2) {
                if (ycount == 0) {
                    if (yearMax < 0.9) {
                        yearMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (yearAverage < (yearMax + 0.02) && yearAverage > yearBelow) {
                        yearAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (yearBelow < yearAverage && yearBelow > (yearLowest - 0.02)) {
                        yearBelow -= 0.02;
                    }
                } else if (ycount == 3) {
                    if (yearLowest < yearBelow && yearLowest > 0.5) {
                        yearLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 3) {
                if (ycount == 0) {
                    if (yearMax < 0.9) {
                        yearMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (yearAverage < (yearMax + 0.02) && yearAverage > yearBelow) {
                        yearAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (yearBelow < yearAverage && yearBelow > (yearLowest - 0.02)) {
                        yearBelow -= 0.02;
                    }
                } else if (ycount == 3) {
                    if (yearLowest < yearBelow && yearLowest > 0.5) {
                        yearLowest -= 0.05;
                    }
                }
            }
            yearArray.remove(max);
            ycount += 1;
        }

        JSONArray cleanlinessActivityArray = activity.getJSONArray(1);
        JSONObject cleanlinessActivity = cleanlinessActivityArray.getJSONObject(0);
        int organizedValue = cleanlinessActivity.getInt("organized");
        int casualValue = cleanlinessActivity.getInt("casual");
        int occMessyValue = cleanlinessActivity.getInt("occasionally_messy");
        int messyValue = cleanlinessActivity.getInt("messy");
        List<Integer> cleanlinessArray = new ArrayList<Integer>(Arrays.asList(organizedValue, casualValue, occMessyValue, messyValue));
        Integer[] tempCleanlinessArray = {organizedValue, casualValue, occMessyValue, messyValue};
        int clcount = 0;
        for (int i = 0; i < cleanlinessArray.size(); i++) {
            int max = (int) Collections.max(cleanlinessArray);
            int indexOfMax = Arrays.asList(tempCleanlinessArray).indexOf(max);
            if (indexOfMax == 0) {
                if (clcount == 0) {
                    if (cleanMax < 0.9) {
                        cleanMax += 0.05;
                    }
                } else if (clcount == 1) {
                    if (cleanAverage < (cleanMax + 0.02) && cleanAverage > cleanBelow) {
                        cleanAverage += 0.02;
                    }
                } else if (clcount == 2) {
                    if (cleanBelow < cleanAverage && cleanBelow > (cleanLowest - 0.02)) {
                        cleanBelow -= 0.02;
                    }
                } else if (clcount == 3) {
                    if (cleanLowest < cleanBelow && cleanLowest > 0.5) {
                        cleanLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 1) {
                if (clcount == 0) {
                    if (cleanMax < 0.9) {
                        cleanMax += 0.05;
                    }
                } else if (clcount == 1) {
                    if (cleanAverage < (cleanMax + 0.02) && cleanAverage > cleanBelow) {
                        cleanAverage += 0.02;
                    }
                } else if (clcount == 2) {
                    if (cleanBelow < cleanAverage && cleanBelow > (cleanLowest - 0.02)) {
                        cleanBelow -= 0.02;
                    }
                } else if (clcount == 3) {
                    if (cleanLowest < cleanBelow && cleanLowest > 0.5) {
                        cleanLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 2) {
                if (clcount == 0) {
                    if (cleanMax < 0.9) {
                        cleanMax += 0.05;
                    }
                } else if (clcount == 1) {
                    if (cleanAverage < (cleanMax + 0.02) && cleanAverage > cleanBelow) {
                        cleanAverage += 0.02;
                    }
                } else if (clcount == 2) {
                    if (cleanBelow < cleanAverage && cleanBelow > (cleanLowest - 0.02)) {
                        cleanBelow -= 0.02;
                    }
                } else if (clcount == 3) {
                    if (cleanLowest < cleanBelow && cleanLowest > 0.5) {
                        cleanLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 3) {
                if (clcount == 0) {
                    if (cleanMax < 0.9) {
                        cleanMax += 0.05;
                    }
                } else if (clcount == 1) {
                    if (cleanAverage < (cleanMax + 0.02) && cleanAverage > cleanBelow) {
                        cleanAverage += 0.02;
                    }
                } else if (clcount == 2) {
                    if (cleanBelow < cleanAverage && cleanBelow > (cleanLowest - 0.02)) {
                        cleanBelow -= 0.02;
                    }
                } else if (clcount == 3) {
                    if (cleanLowest < cleanBelow && cleanLowest > 0.5) {
                        cleanLowest -= 0.05;
                    }
                }
            }
            cleanlinessArray.remove(max);
            clcount += 1;
        }

        JSONArray smokingActivityArray = activity.getJSONArray(2);
        JSONObject smokingActivity = smokingActivityArray.getJSONObject(0);
        int yesValue = smokingActivity.getInt("yes");
        int noValue = smokingActivity.getInt("no");
        int sometimesValue = smokingActivity.getInt("sometimes");
        List<Integer> smokingArray = new ArrayList<Integer>(Arrays.asList(yesValue, noValue, sometimesValue));
        Integer[] tempSmokingArray = {yesValue, noValue, sometimesValue};
        int scount = 0;
        for (int i = 0; i < smokingArray.size(); i++) {
            int max = (int) Collections.max(smokingArray);
            int indexOfMax = Arrays.asList(tempSmokingArray).indexOf(max);
            if (indexOfMax == 0) {
                if (scount == 0) {
                    if (smokingMax < 0.9) {
                        smokingMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (smokingAverage < (smokingMax + 0.02) && smokingAverage > smokingBelow) {
                        smokingAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (smokingBelow < smokingAverage && smokingBelow > 0.05) {
                        yearBelow -= 0.05;
                    }
                }
            } else if (indexOfMax == 1) {
                if (scount == 0) {
                    if (smokingMax < 0.9) {
                        smokingMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (smokingAverage < (smokingMax + 0.02) && smokingAverage > smokingBelow) {
                        smokingAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (smokingBelow < smokingAverage && smokingBelow > 0.05) {
                        yearBelow -= 0.05;
                    }
                }
            } else if (indexOfMax == 2) {
                if (scount == 0) {
                    if (smokingMax < 0.9) {
                        smokingMax += 0.05;
                    }
                } else if (ycount == 1) {
                    if (smokingAverage < (smokingMax + 0.02) && smokingAverage > smokingBelow) {
                        smokingAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (smokingBelow < smokingAverage && smokingBelow > 0.05) {
                        smokingBelow -= 0.05;
                    }
                }
            }
            smokingArray.remove(max);
            scount += 1;
        }

        JSONArray drinkingActivityArray = activity.getJSONArray(3);
        JSONObject drinkingActivity = drinkingActivityArray.getJSONObject(0);
        int yesdValue = drinkingActivity.getInt("yes");
        int nodValue = drinkingActivity.getInt("no");
        int sometimesdValue = drinkingActivity.getInt("sometimes");
        List<Integer> drinkingArray = new ArrayList<Integer>(Arrays.asList(yesdValue, nodValue, sometimesdValue));
        Integer[] tempDrinkingArray = {yesdValue, nodValue, sometimesdValue};
        int dcount = 0;
        for (int i = 0; i < drinkingArray.size(); i++) {
            int max = (int) Collections.max(drinkingArray);
            int indexOfMax = Arrays.asList(tempDrinkingArray).indexOf(max);
            if (indexOfMax == 0) {
                if (dcount == 0) {
                    if (drinkingMax < 0.9) {
                        drinkingMax += 0.05;
                    }
                } else if (dcount == 1) {
                    if (drinkingAverage < (drinkingMax + 0.02) && drinkingAverage > drinkingBelow) {
                        drinkingAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (drinkingBelow < drinkingAverage && drinkingBelow > 0.05) {
                        drinkingBelow -= 0.05;
                    }
                }
            } else if (indexOfMax == 1) {
                if (dcount == 0) {
                    if (drinkingMax < 0.9) {
                        drinkingMax += 0.05;
                    }
                } else if (dcount == 1) {
                    if (drinkingAverage < (drinkingMax + 0.02) && drinkingAverage > drinkingBelow) {
                        drinkingAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (drinkingBelow < drinkingAverage && drinkingBelow > 0.05) {
                        drinkingBelow -= 0.05;
                    }
                }
            } else if (indexOfMax == 2) {
                if (dcount == 0) {
                    if (drinkingMax < 0.9) {
                        drinkingMax += 0.05;
                    }
                } else if (dcount == 1) {
                    if (drinkingAverage < (drinkingMax + 0.02) && drinkingAverage > drinkingBelow) {
                        drinkingAverage += 0.02;
                    }
                } else if (ycount == 2) {
                    if (drinkingBelow < drinkingAverage && drinkingBelow > 0.05) {
                        drinkingBelow -= 0.05;
                    }
                }
            }
            drinkingArray.remove(max);
            dcount += 1;
        }

        JSONArray roomActivityArray = activity.getJSONArray(4);
        JSONObject roomActivity = roomActivityArray.getJSONObject(0);
        int socialValue = roomActivity.getInt("social_space");
        int studyValue = roomActivity.getInt("study_space");
        int sleepingValue = roomActivity.getInt("sleeping_space");
        int allValue = roomActivity.getInt("all_above");
        List<Integer> roomArray = new ArrayList<Integer>(Arrays.asList(socialValue, studyValue, sleepingValue, allValue));
        Integer[] tempRoomArray = {socialValue, studyValue, sleepingValue, allValue};
        int rcount = 0;
        for (int i = 0; i < roomArray.size(); i++) {
            int max = (int) Collections.max(roomArray);
            int indexOfMax = Arrays.asList(tempRoomArray).indexOf(max);
            if (indexOfMax == 0) {
                if (rcount == 0) {
                    if (roomMax < 0.9) {
                        roomMax += 0.05;
                    }
                } else if (rcount == 1) {
                    if (roomAverage < (roomMax + 0.02) && roomAverage > roomBelow) {
                        roomAverage += 0.02;
                    }
                } else if (rcount == 2) {
                    if (roomBelow < roomAverage && roomBelow > (roomLowest - 0.02)) {
                        roomBelow -= 0.02;
                    }
                } else if (rcount == 3) {
                    if (roomLowest < roomBelow && roomLowest > 0.5) {
                        roomLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 1) {
                if (rcount == 0) {
                    if (roomMax < 0.9) {
                        roomMax += 0.05;
                    }
                } else if (rcount == 1) {
                    if (roomAverage < (roomMax + 0.02) && roomAverage > roomBelow) {
                        roomAverage += 0.02;
                    }
                } else if (rcount == 2) {
                    if (roomBelow < roomAverage && roomBelow > (roomLowest - 0.02)) {
                        roomBelow -= 0.02;
                    }
                } else if (rcount == 3) {
                    if (roomLowest < roomBelow && roomLowest > 0.5) {
                        roomLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 2) {
                if (rcount == 0) {
                    if (roomMax < 0.9) {
                        roomMax += 0.05;
                    }
                } else if (rcount == 1) {
                    if (roomAverage < (roomMax + 0.02) && roomAverage > roomBelow) {
                        roomAverage += 0.02;
                    }
                } else if (rcount == 2) {
                    if (roomBelow < roomAverage && roomBelow > (roomLowest - 0.02)) {
                        roomBelow -= 0.02;
                    }
                } else if (rcount == 3) {
                    if (roomLowest < roomBelow && roomLowest > 0.5) {
                        roomLowest -= 0.05;
                    }
                }
            } else if (indexOfMax == 3) {
                if (rcount == 0) {
                    if (roomMax < 0.9) {
                        roomMax += 0.05;
                    }
                } else if (rcount == 1) {
                    if (roomAverage < (roomMax + 0.02) && roomAverage > roomBelow) {
                        roomAverage += 0.02;
                    }
                } else if (rcount == 2) {
                    if (roomBelow < roomAverage && roomBelow > (roomLowest - 0.02)) {
                        roomBelow -= 0.02;
                    }
                } else if (rcount == 3) {
                    if (roomLowest < roomBelow && roomLowest > 0.5) {
                        roomLowest -= 0.05;
                    }
                }
            }
            roomArray.remove(max);
            rcount += 1;
        }

    }
}