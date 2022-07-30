package com.example.campusbud.fragments;

import android.annotation.SuppressLint;
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
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.example.campusbud.adapters.CardAdapter;
import com.example.campusbud.models.Image;
import com.example.campusbud.R;
import com.parse.ParseQuery;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RoommateFragment extends Fragment {

    public Koloda koloda;
    public CardAdapter cardAdapter;
    public List<Image> allImages;
    private User mCurrentUser;
    private User mLoggedInUser;
    private int mPosition;

    private double mYearMax;
    private double mYearAverage;
    private double mYearBelow;
    private double mYearLowest;
    private double mCleanMax;
    private double mCleanAverage;
    private double mCleanBelow;
    private double mCleanLowest;
    private double mSmokingMax;
    private double mSmokingAverage;
    private double mSmokingBelow;
    private double mDrinkingMax;
    private double mDrinkingAverage;
    private double mDrinkingBelow;
    private double mRoomMax;
    private double mRoomAverage;

    private int mUserYearValue;
    private int mLoggedInYearValue;
    private int mUserCleanlinessValue;
    private int mLoggedInCleanlinessValue;
    private int mUserSmokingValue;
    private int mLoggedInSmokingValue;
    private int mUserDrinkingValue;
    private int mLoggedInDrinkingValue;

    private double mYear;
    private double mCleanliness;
    private double mDrinking;
    private double mSmoking;
    private double mRoomUse;
    private double mTimeSleep;
    private double mTimeWake;

    private int mIncrement;
    private int mNumRefreshed;

    private static final String TAG = "RoommateFragment";

    private List<User> mAllUsers;
    public List<User> sortedUsers;
    private List<Double> mRatings;
    private JSONObject mMetadata;

    private JSONObject mUserYearValues;
    private JSONObject mUserCleanlinessValues;
    private JSONObject mUserSmokingValues;
    private JSONObject mUserDrinkingValues;
    private JSONObject mUserRoomUseValues;

    public RoommateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roommate, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoggedInUser = CometChat.getLoggedInUser();
        mMetadata = mLoggedInUser.getMetadata();
        try {
            mNumRefreshed = mMetadata.getInt("num_refreshed");
            JSONArray mUserActivity = mMetadata.getJSONArray("user_activity");
            JSONArray mUserYearActivity = mUserActivity.getJSONArray(0);
            mUserYearValues = mUserYearActivity.getJSONObject(0);
            JSONArray mUserCleanlinessActivity = mUserActivity.getJSONArray(1);
            mUserCleanlinessValues = mUserCleanlinessActivity.getJSONObject(0);
            JSONArray mUserSmokingActivity = mUserActivity.getJSONArray(2);
            mUserSmokingValues = mUserSmokingActivity.getJSONObject(0);
            JSONArray mUserDrinkingActivity = mUserActivity.getJSONArray(3);
            mUserDrinkingValues = mUserDrinkingActivity.getJSONObject(0);
            JSONArray mUserRoomUseActivity = mUserActivity.getJSONArray(4);
            mUserRoomUseValues = mUserRoomUseActivity.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        koloda = view.findViewById(R.id.koloda);
        mAllUsers = new ArrayList<>();
        mRatings = new ArrayList<>();
        sortedUsers = new ArrayList<>();
        allImages = new ArrayList<>();
        queryUsers();
        cardAdapter = new CardAdapter(getContext(), sortedUsers, allImages);
        koloda.setAdapter(cardAdapter);
        Log.d(TAG, "profiles: " + mAllUsers);

        koloda.setKolodaListener(new KolodaListener() {
            @Override
            public void onNewTopCard(int i) {}
            @Override
            public void onCardDrag(int i, @NonNull View view, float v) {}
            @Override
            public void onCardSwipedLeft(int i) {
                mCurrentUser = sortedUsers.get(mPosition);
                try {
                    updateUserLeft(mCurrentUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPosition += 1;
            }
            @Override
            public void onCardSwipedRight(int i) {
                mCurrentUser = sortedUsers.get(mPosition);
                try {
                    updateUserRight(mCurrentUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startUserIntent(mCurrentUser);
                mPosition += 1;
            }
            @Override
            public void onClickRight(int i) {}
            @Override
            public void onClickLeft(int i) {}
            @Override
            public void onCardSingleTap(int i) {}
            @Override
            public void onCardDoubleTap(int i) {}
            @Override
            public void onCardLongPress(int i) {}
            @Override
            public void onEmptyDeck() {}
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

    private void queryUsers() {
        ParseQuery<Image> query = ParseQuery.getQuery(Image.class);
        query.findInBackground((objects, e) -> {
            allImages.addAll(objects);
            UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
            usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    for (int i = 0; i < users.size(); i++) {
                        User mUser = users.get(i);
                        JSONObject mUserMetadata = mUser.getMetadata();
                        try {
                            boolean mSwitched = mUserMetadata.getBoolean("ifSwitched");
                            if (mSwitched) {
                                String mUserCollege = mUserMetadata.getString("college");
                                String mCurrentUserCollege = mMetadata.getString("college");
                                if (mUserCollege.equals(mCurrentUserCollege)) {
                                    String mUserGender = mUserMetadata.getString("gender");
                                    String mCurrentUserGender = mMetadata.getString("gender");
                                    if (mUserGender.equals(mCurrentUserGender)) {
                                        mAllUsers.add(mUser);
                                        if (mNumRefreshed > 0) {
                                            changeRatings();
                                        }
                                        double mUserRate = rate(mUser);
                                        mRatings.add(mUserRate);
                                    }
                                }
                            }
                        } catch (JSONException | ParseException jsonException) {
                            jsonException.printStackTrace();
                        }
                    }
                    for (int i = 0; i < mRatings.size(); i++) {
                        double mMaxValue = Collections.max(mRatings);
                        int mMaxIndex = mRatings.indexOf(mMaxValue);
                        sortedUsers.add(mAllUsers.get(mMaxIndex));
                        mRatings.set(mMaxIndex, 0.0);
                    }
                    mNumRefreshed += 1;
                    try {
                        mMetadata.put("num_refreshed", mNumRefreshed);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    updateUser(mLoggedInUser);
                    cardAdapter.notifyDataSetChanged();
                }
                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
                }
            });
        });
    }

    private void rateYear(JSONObject userData) throws JSONException{
        String mUserYear = userData.getString("year");
        String mLoggedInYear = mMetadata.getString("year");

        switch (mUserYear) {
            case "Senior":
                mUserYearValue = 4;
                break;
            case "Junior":
                mUserYearValue = 3;
                break;
            case "Sophomore":
                mUserYearValue = 2;
                break;
            case "Freshman":
                mUserYearValue = 1;
                break;
        }

        switch (mLoggedInYear) {
            case "Senior":
                mLoggedInYearValue = 4;
                break;
            case "Junior":
                mLoggedInYearValue = 3;
                break;
            case "Sophomore":
                mLoggedInYearValue = 2;
                break;
            case "Freshman":
                mLoggedInYearValue = 1;
                break;
        }

        int mYearDifference = Math.abs(mUserYearValue - mLoggedInYearValue);

        switch (mYearDifference) {
            case 0:
                mYear = mYearMax;
                break;
            case 1:
                mYear = mYearAverage;
                break;
            case 2:
                mYear = mYearBelow;
                break;
            case 3:
                mYear = mYearLowest;
                break;
        }
    }

    private void rateCleanliness(JSONObject userRoommateData, JSONObject loggedInRoommateData) throws JSONException{
        String mUserCleanliness = userRoommateData.getString("cleanliness");
        String mLoggedInCleanliness = loggedInRoommateData.getString("cleanliness");

        switch (mUserCleanliness) {
            case "Organized":
                mUserCleanlinessValue = 4;
                break;
            case "Casual":
                mUserCleanlinessValue = 3;
                break;
            case "Occasionally messy":
                mUserCleanlinessValue = 2;
                break;
            case "Messy":
                mUserCleanlinessValue = 1;
                break;
        }

        switch (mLoggedInCleanliness) {
            case "Organized":
                mLoggedInCleanlinessValue = 4;
                break;
            case "Casual":
                mLoggedInCleanlinessValue = 3;
                break;
            case "Occasionally messy":
                mLoggedInCleanlinessValue = 2;
                break;
            case "Messy":
                mLoggedInCleanlinessValue = 1;
                break;
        }

        int mCleanlinessDifference = Math.abs(mUserCleanlinessValue - mLoggedInCleanlinessValue);

        switch (mCleanlinessDifference) {
            case 0:
                mCleanliness = mCleanMax;
                break;
            case 1:
                mCleanliness = mCleanAverage;
                break;
            case 2:
                mCleanliness = mCleanBelow;
                break;
            case 3:
                mCleanliness = mCleanLowest;
                break;
        }
    }

    private void rateSmoking(JSONObject userRoommateData, JSONObject loggedInRoommateData) throws JSONException {
        String mUserSmoking = userRoommateData.getString("if_smoke");
        String mLoggedInSmoking = loggedInRoommateData.getString("if_smoke");

        switch (mUserSmoking) {
            case "Yes":
                mUserSmokingValue = 3;
                break;
            case "Sometimes":
                mUserSmokingValue = 2;
                break;
            case "No":
                mUserSmokingValue = 1;
                break;
        }

        switch (mLoggedInSmoking) {
            case "Yes":
                mLoggedInSmokingValue = 3;
                break;
            case "Sometimes":
                mLoggedInSmokingValue = 2;
                break;
            case "No":
                mLoggedInSmokingValue = 1;
                break;
        }

        int mSmokingDifference = Math.abs(mUserSmokingValue - mLoggedInSmokingValue);

        switch (mSmokingDifference) {
            case 0:
                mSmoking = mSmokingMax;
                break;
            case 1:
                mSmoking = mSmokingAverage;
                break;
            case 2:
                mSmoking = mSmokingBelow;
        }
    }

    private void rateDrinking(JSONObject userRoommateData, JSONObject loggedInRoommateData) throws JSONException {
        String mUserDrinking = userRoommateData.getString("if_drink");
        String mLoggedInDrinking = loggedInRoommateData.getString("if_drink");

        switch (mUserDrinking) {
            case "Yes":
                mUserDrinkingValue = 3;
                break;
            case "Sometimes":
                mUserDrinkingValue = 2;
                break;
            case "No":
                mUserDrinkingValue = 1;
                break;
        }

        switch (mLoggedInDrinking) {
            case "Yes":
                mLoggedInDrinkingValue = 3;
                break;
            case "Sometimes":
                mLoggedInDrinkingValue = 2;
                break;
            case "No":
                mLoggedInDrinkingValue = 1;
                break;
        }

        int drinkingDifference = Math.abs(mUserDrinkingValue - mLoggedInDrinkingValue);

        switch (drinkingDifference) {
            case 0:
                mDrinking = mDrinkingMax;
                break;
            case 1:
                mDrinking = mDrinkingAverage;
                break;
            case 2:
                mDrinking = mDrinkingBelow;
                break;
        }
    }

    private void rateRoomUse(JSONObject userRoommateData, JSONObject loggedInRoommateData) throws JSONException {
        String mUserRoomUse = userRoommateData.getString("room_use");
        String mLoggedInRoomUse = loggedInRoommateData.getString("room_use");

        if (mUserRoomUse.equals(mLoggedInRoomUse)) {
            mRoomUse = mRoomMax;
        } else {
            mRoomUse = mRoomAverage;
        }
    }

    private void rateSleep(JSONObject userRoommateData, JSONObject loggedInRoommateData) throws JSONException, java.text.ParseException {
        String mUserTimeSleepString = userRoommateData.getString("time_sleep");
        String mLoggedInTimeSleepString = loggedInRoommateData.getString("time_sleep");
        @SuppressLint("SimpleDateFormat") DateFormat mDateFormat = new SimpleDateFormat("hh:mmaa");
        Date mUserTimeSleep = mDateFormat.parse(mUserTimeSleepString);
        Date mLoggedInTimeSleep = mDateFormat.parse(mLoggedInTimeSleepString);
        long mSleepTimeDifference = 0;
        if (mUserTimeSleep != null && mLoggedInTimeSleep != null) {
            mSleepTimeDifference = Math.abs((mUserTimeSleep.getTime() - mLoggedInTimeSleep.getTime()) / (60 * 60 * 1000) % 24);
        }

        double mMax = 0.7;
        double mAverage = 0.5;
        double mBelow = 0.3;
        double mLowest = 0.1;

        if (mSleepTimeDifference == 0) {
            mTimeSleep = mMax;
        } else if (mSleepTimeDifference == 1 || mSleepTimeDifference == 2) {
            mTimeSleep = mAverage;
        } else if (mSleepTimeDifference <= 5) {
            mTimeSleep = mBelow;
        } else {
            mTimeSleep = mLowest;
        }

        String mUserTimeWakeString = userRoommateData.getString("time_wake");
        String mLoggedInTimeWakeString = loggedInRoommateData.getString("time_wake");
        Date mUserTimeWake = mDateFormat.parse(mUserTimeWakeString);
        Date mLoggedInTimeWake = mDateFormat.parse(mLoggedInTimeWakeString);
        long mWakeTimeDifference = 0;
        if (mUserTimeWake != null && mLoggedInTimeWake != null) {
            mWakeTimeDifference = Math.abs((mUserTimeWake.getTime() - mLoggedInTimeWake.getTime()) / (60 * 60 * 1000) % 24);
        }

        if (mWakeTimeDifference == 0) {
            mTimeWake = mMax;
        } else if (mWakeTimeDifference == 1 || mWakeTimeDifference == 2) {
            mTimeWake = mAverage;
        } else if (mWakeTimeDifference <= 5) {
            mTimeWake = mBelow;
        } else {
            mTimeWake = mLowest;
        }
    }

    private double rate(User user) throws JSONException, java.text.ParseException {
        JSONObject mUserData = user.getMetadata();
        JSONArray mUserRoommateProfile = mUserData.getJSONArray("roommate_profile");
        JSONArray mLoggedInRoommateProfile = mMetadata.getJSONArray("roommate_profile");
        JSONObject mUserRoommateData = mUserRoommateProfile.getJSONObject(0);
        JSONObject mLoggedInRoommateData = mLoggedInRoommateProfile.getJSONObject(0);

        rateYear(mUserData);
        rateCleanliness(mUserRoommateData, mLoggedInRoommateData);
        rateSmoking(mUserRoommateData, mLoggedInRoommateData);
        rateDrinking(mUserRoommateData, mLoggedInRoommateData);
        rateRoomUse(mUserRoommateData, mLoggedInRoommateData);
        rateSleep(mUserRoommateData, mLoggedInRoommateData);

        return mYear + mCleanliness + mSmoking + mDrinking + mRoomUse + mTimeSleep + mTimeWake;
    }

    private void updateYearActivity(JSONObject userData, int increment) throws JSONException {
        String mCurrentUserYear = userData.getString("year");
        switch (mCurrentUserYear) {
            case "Freshman": {
                int mCurrYearVal = mUserYearValues.getInt("freshman");
                mCurrYearVal += increment;
                mUserYearValues.remove("freshman");
                mUserYearValues.put("freshman", mCurrYearVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Sophomore": {
                int mCurrYearVal = mUserYearValues.getInt("sophomore");
                mCurrYearVal += increment;
                mUserYearValues.remove("sophomore");
                mUserYearValues.put("sophomore", mCurrYearVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Junior": {
                int mCurrYearVal = mUserYearValues.getInt("junior");
                mCurrYearVal += increment;
                mUserYearValues.remove("junior");
                mUserYearValues.put("junior", mCurrYearVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Senior": {
                int mCurrYearVal = mUserYearValues.getInt("senior");
                mCurrYearVal += increment;
                mUserYearValues.remove("senior");
                mUserYearValues.put("senior", mCurrYearVal);
                updateUser(mLoggedInUser);
                break;
            }
        }
    }

    private void updateCleanlinessActivity(JSONObject roommateData, int increment) throws JSONException{
        String mCurrentUserCleanliness = roommateData.getString("cleanliness");
        switch (mCurrentUserCleanliness) {
            case "Organized": {
                int mCurrCleanlinessVal = mUserCleanlinessValues.getInt("organized");
                mCurrCleanlinessVal += increment;
                mUserCleanlinessValues.remove("organized");
                mUserCleanlinessValues.put("organized", mCurrCleanlinessVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Casual": {
                int mCurrCleanlinessVal = mUserCleanlinessValues.getInt("casual");
                mCurrCleanlinessVal += increment;
                mUserCleanlinessValues.remove("casual");
                mUserCleanlinessValues.put("casual", mCurrCleanlinessVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Occasionally messy": {
                int mCurrCleanlinessVal = mUserCleanlinessValues.getInt("occasionally_messy");
                mCurrCleanlinessVal += increment;
                mUserCleanlinessValues.remove("occasionally_messy");
                mUserCleanlinessValues.put("occasionally_messy", mCurrCleanlinessVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Messy": {
                int mCurrCleanlinessVal = mUserCleanlinessValues.getInt("messy");
                mCurrCleanlinessVal += increment;
                mUserCleanlinessValues.remove("messy");
                mUserCleanlinessValues.put("messy", mCurrCleanlinessVal);
                updateUser(mLoggedInUser);
                break;
            }
        }
    }

    private void updateSmokingActivity(JSONObject roommateData, int increment) throws JSONException{
        String mCurrentUserSmoking = roommateData.getString("if_smoke");
        switch (mCurrentUserSmoking) {
            case "Yes": {
                int mCurrSmokingVal = mUserSmokingValues.getInt("yes");
                mCurrSmokingVal += increment;
                mUserSmokingValues.remove("yes");
                mUserSmokingValues.put("yes", mCurrSmokingVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Sometimes": {
                int mCurrSmokingVal = mUserSmokingValues.getInt("sometimes");
                mCurrSmokingVal += increment;
                mUserSmokingValues.remove("sometimes");
                mUserSmokingValues.put("sometimes", mCurrSmokingVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "No": {
                int mCurrSmokingVal = mUserSmokingValues.getInt("no");
                mCurrSmokingVal += increment;
                mUserSmokingValues.remove("no");
                mUserSmokingValues.put("no", mCurrSmokingVal);
                updateUser(mLoggedInUser);
                break;
            }
        }
    }

    private void updateDrinkingActivity(JSONObject roommateData, int increment) throws JSONException{
        String mCurrentUserDrinking = roommateData.getString("if_drink");
        switch (mCurrentUserDrinking) {
            case "Yes": {
                int mCurrDrinkingVal = mUserDrinkingValues.getInt("yes");
                mCurrDrinkingVal += increment;
                mUserDrinkingValues.remove("yes");
                mUserDrinkingValues.put("yes", mCurrDrinkingVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Sometimes": {
                int mCurrDrinkingVal = mUserDrinkingValues.getInt("sometimes");
                mCurrDrinkingVal += increment;
                mUserDrinkingValues.remove("sometimes");
                mUserDrinkingValues.put("sometimes", mCurrDrinkingVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "No": {
                int mCurrDrinkingVal = mUserDrinkingValues.getInt("no");
                mCurrDrinkingVal += increment;
                mUserDrinkingValues.remove("no");
                mUserDrinkingValues.put("no", mCurrDrinkingVal);
                updateUser(mLoggedInUser);
                break;
            }
        }
    }

    private void updateRoomUseActivity(JSONObject roommateData, int increment) throws JSONException {
        String mCurrentUserRoomUse = roommateData.getString("room_use");
        switch (mCurrentUserRoomUse) {
            case "Social Space": {
                int mCurrRoomUseVal = mUserRoomUseValues.getInt("social_space");
                mCurrRoomUseVal += increment;
                mUserRoomUseValues.remove("social_space");
                mUserRoomUseValues.put("social_space", mCurrRoomUseVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Study Space": {
                int mCurrRoomUseVal = mUserRoomUseValues.getInt("study_space");
                mCurrRoomUseVal += increment;
                mUserRoomUseValues.remove("study_space");
                mUserRoomUseValues.put("study_space", mCurrRoomUseVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "Sleeping Space": {
                int mCurrRoomUseVal = mUserCleanlinessValues.getInt("sleeping_space");
                mCurrRoomUseVal += increment;
                mUserRoomUseValues.remove("sleeping_space");
                mUserRoomUseValues.put("sleeping_space", mCurrRoomUseVal);
                updateUser(mLoggedInUser);
                break;
            }
            case "All of the Above": {
                int mCurrRoomUseVal = mUserCleanlinessValues.getInt("all_above");
                mCurrRoomUseVal += increment;
                mUserRoomUseValues.remove("all_above");
                mUserRoomUseValues.put("all_above", mCurrRoomUseVal);
                updateUser(mLoggedInUser);
                break;
            }
        }
    }

    private void updateActivity(User currentUser, int increment) throws JSONException {
        JSONObject mCurrentUserData = currentUser.getMetadata();
        JSONArray mCurrentUserRoommateProfile = mCurrentUserData.getJSONArray("roommate_profile");
        JSONObject mCurrentUserRoommateData = mCurrentUserRoommateProfile.getJSONObject(0);

        updateYearActivity(mCurrentUserData, increment);
        updateCleanlinessActivity(mCurrentUserRoommateData, increment);
        updateSmokingActivity(mCurrentUserRoommateData, increment);
        updateDrinkingActivity(mCurrentUserRoommateData, increment);
        updateRoomUseActivity(mCurrentUserRoommateData, increment);
    }

    private void updateUserLeft(User currentUser) throws JSONException {
        mIncrement = -1;
        updateActivity(currentUser, mIncrement);
    }

    private void updateUserRight(User currentUser) throws JSONException {
        mIncrement = 1;
        updateActivity(currentUser, mIncrement);
    }

    private void changeYearRates(JSONObject rateValues, JSONArray activity) throws JSONException {
        mYearMax = rateValues.getDouble("year_max");
        mYearAverage = rateValues.getDouble("year_average");
        mYearBelow = rateValues.getDouble("year_below");
        mYearLowest = rateValues.getDouble("year_lowest");

        JSONArray mYearActivityArray = activity.getJSONArray(0);
        JSONObject mYearActivity = mYearActivityArray.getJSONObject(0);
        int mFreshValue = mYearActivity.getInt("freshman");
        int mSophValue = mYearActivity.getInt("sophomore");
        int mJunValue = mYearActivity.getInt("junior");
        int mSenValue = mYearActivity.getInt("senior");
        List<Integer> mYearArray = new ArrayList<>(Arrays.asList(mFreshValue, mSophValue, mJunValue, mSenValue));
        Integer[] mTempYearArray = {mFreshValue, mSophValue, mJunValue, mSenValue};
        int mYcount = 0;
        for (int i = 0; i < mTempYearArray.length; i++) {
            int mMax = Collections.max(mYearArray);
            int mIndexOfMax = Arrays.asList(mTempYearArray).indexOf(mMax);
            switch (mIndexOfMax) {
                case 0:
                    switch (mYcount) {
                        case 0:
                            if (mYearMax > mYearAverage && mYearMax < 0.9) {
                                mYearMax += 0.05;
                                rateValues.put("year_max", mYearMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mYearAverage < (mYearMax + 0.02) && mYearAverage > mYearBelow) {
                                mYearAverage += 0.02;
                                rateValues.put("year_average", mYearAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mYearBelow < mYearAverage && mYearBelow > (mYearLowest - 0.02)) {
                                mYearBelow -= 0.02;
                                rateValues.put("year_below", mYearBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mYearLowest < mYearBelow && mYearLowest > 0.5) {
                                mYearLowest -= 0.05;
                                rateValues.put("year_lowest", mYearLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 1:
                    switch (mYcount) {
                        case 0:
                            if (mYearMax > mYearAverage && mYearMax < 0.9) {
                                mYearMax += 0.05;
                                rateValues.put("year_max", mYearMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mYearAverage < (mYearMax + 0.02) && mYearAverage > mYearBelow) {
                                mYearAverage += 0.02;
                                rateValues.put("year_average", mYearAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mYearBelow < mYearAverage && mYearBelow > (mYearLowest - 0.02)) {
                                mYearBelow -= 0.02;
                                rateValues.put("year_below", mYearBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mYearLowest < mYearBelow && mYearLowest > 0.5) {
                                mYearLowest -= 0.05;
                                rateValues.put("year_lowest", mYearLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 2:
                    switch (mYcount) {
                        case 0:
                            if (mYearMax > mYearAverage && mYearMax < 0.9) {
                                mYearMax += 0.05;
                                rateValues.put("year_max", mYearMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mYearAverage < (mYearMax + 0.02) && mYearAverage > mYearBelow) {
                                mYearAverage += 0.02;
                                rateValues.put("year_average", mYearAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mYearBelow < mYearAverage && mYearBelow > (mYearLowest - 0.02)) {
                                mYearBelow -= 0.02;
                                rateValues.put("year_below", mYearBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mYearLowest < mYearBelow && mYearLowest > 0.5) {
                                mYearLowest -= 0.05;
                                rateValues.put("year_lowest", mYearLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 3:
                    switch (mYcount) {
                        case 0:
                            if (mYearMax > mYearAverage && mYearMax < 0.9) {
                                mYearMax += 0.05;
                                rateValues.put("year_max", mYearMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mYearAverage < (mYearMax + 0.02) && mYearAverage > mYearBelow) {
                                mYearAverage += 0.02;
                                rateValues.put("year_average", mYearAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mYearBelow < mYearAverage && mYearBelow > (mYearLowest - 0.02)) {
                                mYearBelow -= 0.02;
                                rateValues.put("year_below", mYearBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mYearLowest < mYearBelow && mYearLowest > 0.5) {
                                mYearLowest -= 0.05;
                                rateValues.put("year_lowest", mYearLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
            }
            mYearArray.set(mIndexOfMax, -9999);
            mYcount += 1;
        }
    }

    private void changeCleanlinessRates(JSONObject rateValues, JSONArray activity) throws JSONException {
        mCleanMax = rateValues.getDouble("clean_max");
        mCleanAverage = rateValues.getDouble("clean_average");
        mCleanBelow = rateValues.getDouble("clean_below");
        mCleanLowest = rateValues.getDouble("clean_lowest");

        JSONArray mCleanlinessActivityArray = activity.getJSONArray(1);
        JSONObject mCleanlinessActivity = mCleanlinessActivityArray.getJSONObject(0);
        int mOrganizedValue = mCleanlinessActivity.getInt("organized");
        int mCasualValue = mCleanlinessActivity.getInt("casual");
        int mOccMessyValue = mCleanlinessActivity.getInt("occasionally_messy");
        int mMessyValue = mCleanlinessActivity.getInt("messy");
        List<Integer> mCleanlinessArray = new ArrayList<>(Arrays.asList(mOrganizedValue, mCasualValue, mOccMessyValue, mMessyValue));
        Integer[] mTempCleanlinessArray = {mOrganizedValue, mCasualValue, mOccMessyValue, mMessyValue};
        int mClcount = 0;
        for (int i = 0; i < mTempCleanlinessArray.length; i++) {
            int mMax = Collections.max(mCleanlinessArray);
            int mIndexOfMax = Arrays.asList(mTempCleanlinessArray).indexOf(mMax);
            switch (mIndexOfMax) {
                case 0:
                    switch (mClcount) {
                        case 0:
                            if (mCleanMax > mCleanAverage && mCleanMax < 0.9) {
                                mCleanMax += 0.05;
                                rateValues.put("clean_max", mCleanMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mCleanAverage < (mCleanMax + 0.02) && mCleanAverage > mCleanBelow) {
                                mCleanAverage += 0.02;
                                rateValues.put("clean_average", mCleanAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mCleanBelow < mCleanAverage && mCleanBelow > (mCleanLowest - 0.02)) {
                                mCleanBelow -= 0.02;
                                rateValues.put("clean_below", mCleanBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mCleanLowest < mCleanBelow && mCleanLowest > 0.5) {
                                mCleanLowest -= 0.05;
                                rateValues.put("clean_lowest", mCleanLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 1:
                    switch (mClcount) {
                        case 0:
                            if (mCleanMax > mCleanAverage && mCleanMax < 0.9) {
                                mCleanMax += 0.05;
                                rateValues.put("clean_max", mCleanMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mCleanAverage < (mCleanMax + 0.02) && mCleanAverage > mCleanBelow) {
                                mCleanAverage += 0.02;
                                rateValues.put("clean_average", mCleanAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mCleanBelow < mCleanAverage && mCleanBelow > (mCleanLowest - 0.02)) {
                                mCleanBelow -= 0.02;
                                rateValues.put("clean_below", mCleanBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mCleanLowest < mCleanBelow && mCleanLowest > 0.5) {
                                mCleanLowest -= 0.05;
                                rateValues.put("clean_lowest", mCleanLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 2:
                    switch (mClcount) {
                        case 0:
                            if (mCleanMax > mCleanAverage && mCleanMax < 0.9) {
                                mCleanMax += 0.05;
                                rateValues.put("clean_max", mCleanMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mCleanAverage < (mCleanMax + 0.02) && mCleanAverage > mCleanBelow) {
                                mCleanAverage += 0.02;
                                rateValues.put("clean_average", mCleanAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mCleanBelow < mCleanAverage && mCleanBelow > (mCleanLowest - 0.02)) {
                                mCleanBelow -= 0.02;
                                rateValues.put("clean_below", mCleanBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mCleanLowest < mCleanBelow && mCleanLowest > 0.5) {
                                mCleanLowest -= 0.05;
                                rateValues.put("clean_lowest", mCleanLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 3:
                    switch (mClcount) {
                        case 0:
                            if (mCleanMax > mCleanAverage && mCleanMax < 0.9) {
                                mCleanMax += 0.05;
                                rateValues.put("clean_max", mCleanMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mCleanAverage < (mCleanMax + 0.02) && mCleanAverage > mCleanBelow) {
                                mCleanAverage += 0.02;
                                rateValues.put("clean_average", mCleanAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mCleanBelow < mCleanAverage && mCleanBelow > (mCleanLowest - 0.02)) {
                                mCleanBelow -= 0.02;
                                rateValues.put("clean_below", mCleanBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mCleanLowest < mCleanBelow && mCleanLowest > 0.5) {
                                mCleanLowest -= 0.05;
                                rateValues.put("clean_lowest", mCleanLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
            }
            mCleanlinessArray.set(mIndexOfMax, -99999);
            mClcount += 1;
        }
    }

    private void changeDrinkingRates(JSONObject rateValues, JSONArray activity) throws JSONException {
        mDrinkingMax = rateValues.getDouble("drink_max");
        mDrinkingAverage = rateValues.getDouble("drink_average");
        mDrinkingBelow = rateValues.getDouble("drink_below");

        JSONArray mDrinkingActivityArray = activity.getJSONArray(3);
        JSONObject mDrinkingActivity = mDrinkingActivityArray.getJSONObject(0);
        int mYesdValue = mDrinkingActivity.getInt("yes");
        int mNodValue = mDrinkingActivity.getInt("no");
        int mSometimesdValue = mDrinkingActivity.getInt("sometimes");
        List<Integer> mDrinkingArray = new ArrayList<>(Arrays.asList(mYesdValue, mNodValue, mSometimesdValue));
        Integer[] mTempDrinkingArray = {mYesdValue, mNodValue, mSometimesdValue};
        int mDcount = 0;
        for (int i = 0; i < mDrinkingArray.size(); i++) {
            int mMax = Collections.max(mDrinkingArray);
            int mIndexOfMax = Arrays.asList(mTempDrinkingArray).indexOf(mMax);
            switch (mIndexOfMax) {
                case 0:
                    switch (mDcount) {
                        case 0:
                            if (mDrinkingMax > mDrinkingAverage && mDrinkingMax < 0.9) {
                                mDrinkingMax += 0.05;
                                rateValues.put("drink_max", mDrinkingMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mDrinkingAverage < (mDrinkingMax + 0.02) && mDrinkingAverage > mDrinkingBelow) {
                                mDrinkingAverage += 0.02;
                                rateValues.put("drink_average", mDrinkingAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mDrinkingBelow < mDrinkingAverage && mDrinkingBelow > 0.05) {
                                mDrinkingBelow -= 0.05;
                                rateValues.put("drink_below", mDrinkingBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 1:
                    switch (mDcount) {
                        case 0:
                            if (mDrinkingMax > mDrinkingAverage && mDrinkingMax < 0.9) {
                                mDrinkingMax += 0.05;
                                rateValues.put("drink_max", mDrinkingMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mDrinkingAverage < (mDrinkingMax + 0.02) && mDrinkingAverage > mDrinkingBelow) {
                                mDrinkingAverage += 0.02;
                                rateValues.put("drink_average", mDrinkingAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mDrinkingBelow < mDrinkingAverage && mDrinkingBelow > 0.05) {
                                mDrinkingBelow -= 0.05;
                                rateValues.put("drink_below", mDrinkingBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 2:
                    switch (mDcount) {
                        case 0:
                            if (mDrinkingMax > mDrinkingAverage && mDrinkingMax < 0.9) {
                                mDrinkingMax += 0.05;
                                rateValues.put("drink_max", mDrinkingMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mDrinkingAverage < (mDrinkingMax + 0.02) && mDrinkingAverage > mDrinkingBelow) {
                                mDrinkingAverage += 0.02;
                                rateValues.put("drink_average", mDrinkingAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mDrinkingBelow < mDrinkingAverage && mDrinkingBelow > 0.05) {
                                mDrinkingBelow -= 0.05;
                                rateValues.put("drink_below", mDrinkingBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
            }
            mDrinkingArray.set(mIndexOfMax, -9999);
            mDcount += 1;
        }
    }

    private void changeSmokingRates(JSONObject rateValues, JSONArray activity) throws JSONException {
        mSmokingMax = rateValues.getDouble("smoke_max");
        mSmokingAverage = rateValues.getDouble("smoke_average");
        mSmokingBelow = rateValues.getDouble("smoke_below");

        JSONArray mSmokingActivityArray = activity.getJSONArray(2);
        JSONObject mSmokingActivity = mSmokingActivityArray.getJSONObject(0);
        int mYesValue = mSmokingActivity.getInt("yes");
        int mNoValue = mSmokingActivity.getInt("no");
        int mSometimesValue = mSmokingActivity.getInt("sometimes");
        List<Integer> mSmokingArray = new ArrayList<>(Arrays.asList(mYesValue, mNoValue, mSometimesValue));
        Integer[] mTempSmokingArray = {mYesValue, mNoValue, mSometimesValue};
        int mScount = 0;
        for (int i = 0; i < mSmokingArray.size(); i++) {
            int max = Collections.max(mSmokingArray);
            int indexOfMax = Arrays.asList(mTempSmokingArray).indexOf(max);
            switch (indexOfMax) {
                case 0:
                    switch (mScount) {
                        case 0:
                            if (mSmokingMax > mSmokingAverage && mSmokingMax < 0.9) {
                                mSmokingMax += 0.05;
                                rateValues.put("smoke_max", mSmokingMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mSmokingAverage < (mSmokingMax + 0.02) && mSmokingAverage > mSmokingBelow) {
                                mSmokingAverage += 0.02;
                                rateValues.put("smoke_average", mSmokingAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mSmokingBelow < mSmokingAverage && mSmokingBelow > 0.05) {
                                mYearBelow -= 0.05;
                                rateValues.put("smoke_below", mSmokingBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 1:
                    switch (mScount) {
                        case 0:
                            if (mSmokingMax > mSmokingAverage && mSmokingMax < 0.9) {
                                mSmokingMax += 0.05;
                                rateValues.put("smoke_max", mSmokingMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mSmokingAverage < (mSmokingMax + 0.02) && mSmokingAverage > mSmokingBelow) {
                                mSmokingAverage += 0.02;
                                rateValues.put("smoke_average", mSmokingAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mSmokingBelow < mSmokingAverage && mSmokingBelow > 0.05) {
                                mYearBelow -= 0.05;
                                rateValues.put("smoke_below", mSmokingBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 2:
                    switch (mScount) {
                        case 0:
                            if (mSmokingMax > mSmokingAverage && mSmokingMax < 0.9) {
                                mSmokingMax += 0.05;
                                rateValues.put("smoke_max", mSmokingMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mSmokingAverage < (mSmokingMax + 0.02) && mSmokingAverage > mSmokingBelow) {
                                mSmokingAverage += 0.02;
                                rateValues.put("smoke_average", mSmokingAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mSmokingBelow < mSmokingAverage && mSmokingBelow > 0.05) {
                                mSmokingBelow -= 0.05;
                                rateValues.put("smoke_below", mSmokingBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
            }
            mSmokingArray.set(indexOfMax, -9999);
            mScount += 1;
        }
    }

    private void changeRoomUseRates(JSONObject rateValues, JSONArray activity) throws JSONException {
        mRoomMax = rateValues.getDouble("room_max");
        mRoomAverage = rateValues.getDouble("room_average");
        double mRoomBelow = rateValues.getDouble("room_below");
        double mRoomLowest = rateValues.getDouble("room_lowest");

        JSONArray mRoomActivityArray = activity.getJSONArray(4);
        JSONObject mRoomActivity = mRoomActivityArray.getJSONObject(0);
        int mSocialValue = mRoomActivity.getInt("social_space");
        int mStudyValue = mRoomActivity.getInt("study_space");
        int mSleepingValue = mRoomActivity.getInt("sleeping_space");
        int mAllValue = mRoomActivity.getInt("all_above");
        List<Integer> mRoomArray = new ArrayList<>(Arrays.asList(mSocialValue, mStudyValue, mSleepingValue, mAllValue));
        Integer[] mTempRoomArray = {mSocialValue, mStudyValue, mSleepingValue, mAllValue};
        int mRcount = 0;
        for (int i = 0; i < mRoomArray.size(); i++) {
            int mMax = Collections.max(mRoomArray);
            int mIndexOfMax = Arrays.asList(mTempRoomArray).indexOf(mMax);
            switch (mIndexOfMax) {
                case 0:
                    switch (mRcount) {
                        case 0:
                            if (mRoomMax > mRoomAverage && mRoomMax < 0.9) {
                                mRoomMax += 0.05;
                                rateValues.put("room_max", mRoomMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mRoomAverage < (mRoomMax + 0.02) && mRoomAverage > mRoomBelow) {
                                mRoomAverage += 0.02;
                                rateValues.put("room_average", mRoomAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mRoomBelow < mRoomAverage && mRoomBelow > (mRoomLowest - 0.02)) {
                                mRoomBelow -= 0.02;
                                rateValues.put("room_below", mRoomBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mRoomLowest < mRoomBelow && mRoomLowest > 0.5) {
                                mRoomLowest -= 0.05;
                                rateValues.put("room_lowest", mRoomLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 1:
                    switch (mRcount) {
                        case 0:
                            if (mRoomMax > mRoomAverage && mRoomMax < 0.9) {
                                mRoomMax += 0.05;
                                rateValues.put("room_max", mRoomMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mRoomAverage < (mRoomMax + 0.02) && mRoomAverage > mRoomBelow) {
                                mRoomAverage += 0.02;
                                rateValues.put("room_average", mRoomAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mRoomBelow < mRoomAverage && mRoomBelow > (mRoomLowest - 0.02)) {
                                mRoomBelow -= 0.02;
                                rateValues.put("room_below", mRoomBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mRoomLowest < mRoomBelow && mRoomLowest > 0.5) {
                                mRoomLowest -= 0.05;
                                rateValues.put("room_lowest", mRoomLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 2:
                    switch (mRcount) {
                        case 0:
                            if (mRoomMax > mRoomAverage && mRoomMax < 0.9) {
                                mRoomMax += 0.05;
                                rateValues.put("room_max", mRoomMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mRoomAverage < (mRoomMax + 0.02) && mRoomAverage > mRoomBelow) {
                                mRoomAverage += 0.02;
                                rateValues.put("room_average", mRoomAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mRoomBelow < mRoomAverage && mRoomBelow > (mRoomLowest - 0.02)) {
                                mRoomBelow -= 0.02;
                                rateValues.put("room_below", mRoomBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mRoomLowest < mRoomBelow && mRoomLowest > 0.5) {
                                mRoomLowest -= 0.05;
                                rateValues.put("room_lowest", mRoomLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
                case 3:
                    switch (mRcount) {
                        case 0:
                            if (mRoomMax > mRoomAverage && mRoomMax < 0.9) {
                                mRoomMax += 0.05;
                                rateValues.put("room_max", mRoomMax);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 1:
                            if (mRoomAverage < (mRoomMax + 0.02) && mRoomAverage > mRoomBelow) {
                                mRoomAverage += 0.02;
                                rateValues.put("room_average", mRoomAverage);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 2:
                            if (mRoomBelow < mRoomAverage && mRoomBelow > (mRoomLowest - 0.02)) {
                                mRoomBelow -= 0.02;
                                rateValues.put("room_below", mRoomBelow);
                                updateUser(mLoggedInUser);
                            }
                            break;
                        case 3:
                            if (mRoomLowest < mRoomBelow && mRoomLowest > 0.5) {
                                mRoomLowest -= 0.05;
                                rateValues.put("room_lowest", mRoomLowest);
                                updateUser(mLoggedInUser);
                            }
                            break;
                    }
            }
            mRoomArray.set(mIndexOfMax, -9999);
            mRcount += 1;
        }
    }

    private void changeRatings() throws JSONException {
        JSONArray currentUserRates = mMetadata.getJSONArray("rate_values");
        JSONObject currentUserRateValues = currentUserRates.getJSONObject(0);
        JSONArray activity = mMetadata.getJSONArray("user_activity");

        changeYearRates(currentUserRateValues, activity);
        changeCleanlinessRates(currentUserRateValues, activity);
        changeDrinkingRates(currentUserRateValues, activity);
        changeSmokingRates(currentUserRateValues, activity);
        changeRoomUseRates(currentUserRateValues, activity);
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