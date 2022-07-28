package com.example.campusbud.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cometchat.pro.models.User;
import com.example.campusbud.Image;
import com.example.campusbud.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        JSONObject mMetadata = userProfile.getMetadata();
        JSONArray mRoommateProfileArray = mMetadata.getJSONArray("roommate_profile");
        JSONObject mRoommateProfile = mRoommateProfileArray.getJSONObject(0);

        TextView mTvRoommateName = v.findViewById(R.id.tvRName);
        TextView mTvRMajor = v.findViewById(R.id.tvRMajor);
        TextView mTvRYear = v.findViewById(R.id.tvRYear);
        TextView mTvRCleanlinessInput = v.findViewById(R.id.tvRCleanlinessInput);
        TextView mTvRSmokingInput = v.findViewById(R.id.tvRSmokingInput);
        TextView mTvRDrinkingInput = v.findViewById(R.id.tvRDrinkingInput);
        TextView mTvRRoomUseInput = v.findViewById(R.id.tvRRoomUseInput);
        TextView mTvRTimeSleepInput = v.findViewById(R.id.tvRTimeSleepInput);
        TextView mTvRTimeWakeInput = v.findViewById(R.id.tvRTimeWakeInput);
        TextView mTvRInterestsInput1 = v.findViewById(R.id.tvRInterestsInput1);
        TextView mTvRInterestsInput2 = v.findViewById(R.id.tvRInterestsInput2);
        TextView mTvRInterestsInput3 = v.findViewById(R.id.tvRInterestsInput3);
        TextView mTvRActivitiesInput1 = v.findViewById(R.id.tvRActivitiesInput1);
        TextView mTvRActivitiesInput2 = v.findViewById(R.id.tvRActivitiesInput2);
        TextView mTvRActivitiesInput3 = v.findViewById(R.id.tvRActivitiesInput3);
        TextView mTvRBioInput = v.findViewById(R.id.tvRBioInput);
        ImageView mIvRImage1 = v.findViewById(R.id.ivRImage1);
        ImageView mIvRImage2 = v.findViewById(R.id.ivRImage2);
        ImageView mIvRImage3 = v.findViewById(R.id.ivRImage3);

        mTvRoommateName.setText(mMetadata.getString("name"));
        mTvRYear.setText(mMetadata.getString("year"));
        mTvRMajor.setText(mMetadata.getString("major"));
        mTvRCleanlinessInput.setText(mRoommateProfile.getString("cleanliness"));
        mTvRSmokingInput.setText(mRoommateProfile.getString("if_smoke"));
        mTvRDrinkingInput.setText(mRoommateProfile.getString("if_drink"));
        mTvRRoomUseInput.setText(mRoommateProfile.getString("room_use"));
        mTvRTimeSleepInput.setText(mRoommateProfile.getString("time_sleep"));
        mTvRTimeWakeInput.setText(mRoommateProfile.getString("time_wake"));
        mTvRInterestsInput1.setText(mRoommateProfile.getString("interest1"));
        mTvRInterestsInput2.setText(mRoommateProfile.getString("interest2"));
        mTvRInterestsInput3.setText(mRoommateProfile.getString("interest3"));
        mTvRActivitiesInput1.setText(mRoommateProfile.getString("activity1"));
        mTvRActivitiesInput2.setText(mRoommateProfile.getString("activity2"));
        mTvRActivitiesInput3.setText(mRoommateProfile.getString("activity3"));
        mTvRBioInput.setText(mRoommateProfile.getString("bio"));

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
            ParseFile mImage2file = (mImages.get(mIndex)).getImage2Url();
            Glide.with(mContext).load(mImage2file.getUrl()).into(mIvRImage2);
            Log.d(TAG, "Loaded Image 2");
            mContext = mIvRImage3.getContext();
            ParseFile mImage3file = (mImages.get(mIndex)).getImage3Url();
            Glide.with(mContext).load(mImage3file.getUrl()).into(mIvRImage3);
            Log.d(TAG, "Loaded Image 3");
        }
    }
}