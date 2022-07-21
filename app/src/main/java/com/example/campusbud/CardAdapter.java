package com.example.campusbud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.example.campusbud.fragments.RoommateFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yalantis.library.Koloda;
import com.yalantis.library.KolodaListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends BaseAdapter {

    private Context mContext;
    private final List<User> mUserProfiles;
    private final List<Image> mImages;
    private User mUserProfile;
    private int mIndex;

    private final String TAG = "CardAdapter";

    public CardAdapter(Context context, List<User> userProfiles, List<Image> images) {
        this.mContext = context;
        this.mUserProfiles = userProfiles;
        this.mImages = images;
    }

    @Override
    public int getCount() {
        return mUserProfiles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView;
        if (convertView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.item_koloda, parent, false);
            mUserProfile = mUserProfiles.get(position);
            Log.d(TAG, "user: " + mUserProfile);

            try {
                setup(mUserProfile, mView);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            mView = convertView;
        }
        return mView;
    }

    private void setup(User userProfile, View v) throws JSONException {
        JSONObject metadata = userProfile.getMetadata();
        JSONArray roommateProfileArray = metadata.getJSONArray("roommate_profile");
        JSONObject roommateProfile = roommateProfileArray.getJSONObject(0);

        ImageView mIvBackground = v.findViewById(R.id.ivBackground);
        TextView mTvRoommateName = v.findViewById(R.id.tvRName);
        mIvBackground = v.findViewById(R.id.ivBackground);
        TextView mTvRMajor = v.findViewById(R.id.tvRMajor);
        TextView mTvRYear = v.findViewById(R.id.tvRYear);
        TextView mTvRCleanlinessInput = v.findViewById(R.id.tvRCleanlinessInput);
        TextView mTvRSmokingInput = v.findViewById(R.id.tvRSmokingInput);
        TextView mTvRDrinkingInput = v.findViewById(R.id.tvRDrinkingInput);
        TextView mTvRRoomUseInput = v.findViewById(R.id.tvRRoomUseInput);
        TextView mTvRTimeSleepInput = v.findViewById(R.id.tvRTimeSleepInput);
        TextView mTvRTimeWakeInput = v.findViewById(R.id.tvRTimeWakeInput);
        TextView mTvRInterestsInput = v.findViewById(R.id.tvRInterestsInput);
        TextView mTvRActivitiesInput = v.findViewById(R.id.tvRActivitiesInput);
        TextView mTvRBioInput = v.findViewById(R.id.tvRBioInput);
        ImageView mIvRImage1 = v.findViewById(R.id.ivRImage1);
        ImageView mIvRImage2 = v.findViewById(R.id.ivRImage2);
        ImageView mIvRImage3 = v.findViewById(R.id.ivRImage3);

        mTvRoommateName.setText(metadata.getString("name"));
        mTvRYear.setText(metadata.getString("year"));
        mTvRMajor.setText(metadata.getString("major"));
        mTvRCleanlinessInput.setText(roommateProfile.getString("cleanliness"));
        mTvRSmokingInput.setText(roommateProfile.getString("if_smoke"));
        mTvRDrinkingInput.setText(roommateProfile.getString("if_drink"));
        mTvRRoomUseInput.setText(roommateProfile.getString("room_use"));
        mTvRTimeSleepInput.setText(roommateProfile.getString("time_sleep"));
        mTvRTimeWakeInput.setText(roommateProfile.getString("time_wake"));
        mTvRInterestsInput.setText(roommateProfile.getString("interests"));
        mTvRActivitiesInput.setText(roommateProfile.getString("activities"));
        mTvRBioInput.setText(roommateProfile.getString("bio"));

        if (mImages.size() > 0) {
            for (int i = mImages.size() - 1; i >= 0; i--) {
                ParseUser mParseUser = mImages.get(i).getUser();
                String mParseUID = mParseUser.getObjectId().toLowerCase();
                String mCometUID = mUserProfile.getUid();
                if (mParseUID.equals(mCometUID)) {
                    mIndex = i;
                    break;
                }
            }
            mContext = mIvRImage1.getContext();
            ParseFile mImage1file = mImages.get(mIndex).getImage1Url();
            Log.d(TAG, "Image URL: " + mImage1file.getUrl());
            Glide.with(mContext).load(mImage1file.getUrl()).into(mIvRImage1);
            Log.d(TAG, "Loaded Image 1");
            mContext = mIvRImage2.getContext();
            ParseFile image2file = (mImages.get(mIndex)).getImage2Url();
            Glide.with(mContext).load(image2file.getUrl()).into(mIvRImage2);
            Log.d(TAG, "Loaded Image 2");
            mContext = mIvRImage3.getContext();
            ParseFile image3file = (mImages.get(mIndex)).getImage3Url();
            Glide.with(mContext).load(image3file.getUrl()).into(mIvRImage3);
            Log.d(TAG, "Loaded Image 3");
        }
    }
}