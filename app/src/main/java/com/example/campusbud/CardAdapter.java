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

    private Context context;
    public List<User> userProfiles;
    public List<Image> images;
    public User userProfile;
    public int index;
    public Koloda koloda;

    private final String TAG = "CardAdapter";

    public ImageView ivRImage1;
    public ImageView ivRImage2;
    public ImageView ivRImage3;
    public TextView tvRoommateName;
    public ImageView ivBackground;
    public TextView tvEnd;
    public TextView tvRMajor;
    public TextView tvRYear;
    public TextView tvRCleanlinessInput;
    public TextView tvRSmokingInput;
    public TextView tvRDrinkingInput;
    public TextView tvRRoomUseInput;
    public TextView tvRTimeSleepInput;
    public TextView tvRTimeWakeInput;
    public TextView tvRInterestsInput;
    public TextView tvRActivitiesInput;
    public TextView tvRBioInput;


    public CardAdapter(Context context, Koloda koloda, List<User> userProfiles, List<Image> images) {
        this.context = context;
        this.koloda = koloda;
        this.userProfiles = userProfiles;
        this.images = images;
    }

    @Override
    public int getCount() {
        return userProfiles.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    protected void queryProfiles() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                userProfiles = users;
                Log.d(TAG, "User list received: " + users.size());
                Log.d(TAG, "Users" + users);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_koloda, parent, false);
            queryProfiles();
            userProfile = userProfiles.get(position);
            Log.d(TAG, "user: " + userProfile);

            try {
                setup(userProfile, view);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            view = convertView;
        }
        return view;
    }

    public void setup(User userProfile, View v) throws JSONException {
        JSONObject metadata = userProfile.getMetadata();
        JSONArray roommateProfileArray = metadata.getJSONArray("roommate_profile");
        JSONObject roommateProfile = roommateProfileArray.getJSONObject(0);

        ivBackground = v.findViewById(R.id.ivBackground);
        tvEnd = v.findViewById(R.id.tvEnd);
        tvRoommateName = v.findViewById(R.id.tvRName);
        ivBackground = v.findViewById(R.id.ivBackground);
        tvRMajor = v.findViewById(R.id.tvRMajor);
        tvRYear = v.findViewById(R.id.tvRYear);
        tvRCleanlinessInput = v.findViewById(R.id.tvRCleanlinessInput);
        tvRSmokingInput = v.findViewById(R.id.tvRSmokingInput);
        tvRDrinkingInput = v.findViewById(R.id.tvRDrinkingInput);
        tvRRoomUseInput = v.findViewById(R.id.tvRRoomUseInput);
        tvRTimeSleepInput = v.findViewById(R.id.tvRTimeSleepInput);
        tvRTimeWakeInput = v.findViewById(R.id.tvRTimeWakeInput);
        tvRInterestsInput = v.findViewById(R.id.tvRInterestsInput);
        tvRActivitiesInput = v.findViewById(R.id.tvRActivitiesInput);
        tvRBioInput = v.findViewById(R.id.tvRBioInput);
        ivRImage1 = v.findViewById(R.id.ivRImage1);
        ivRImage2 = v.findViewById(R.id.ivRImage2);
        ivRImage3 = v.findViewById(R.id.ivRImage3);

        tvRoommateName.setText(metadata.getString("name"));
        tvRYear.setText(metadata.getString("year"));
        tvRMajor.setText(metadata.getString("major"));
        tvRCleanlinessInput.setText(roommateProfile.getString("cleanliness"));
        tvRSmokingInput.setText(roommateProfile.getString("if_smoke"));
        tvRDrinkingInput.setText(roommateProfile.getString("if_drink"));
        tvRRoomUseInput.setText(roommateProfile.getString("room_use"));
        tvRTimeSleepInput.setText(roommateProfile.getString("time_sleep"));
        tvRTimeWakeInput.setText(roommateProfile.getString("time_wake"));
        tvRInterestsInput.setText(roommateProfile.getString("interests"));
        tvRActivitiesInput.setText(roommateProfile.getString("activities"));
        tvRBioInput.setText(roommateProfile.getString("bio"));

        if (images.size() > 0) {
            for (int i = images.size() - 1; i >= 0; i--) {
                ParseUser parseUser = images.get(i).getUser();
                String parseUID = parseUser.getObjectId().toLowerCase();
                String cometUID = userProfile.getUid();
                if (parseUID.equals(cometUID)) {
                    index = i;
                    break;
                }
            }
            context = ivRImage1.getContext();
            ParseFile image1file = images.get(index).getImage1Url();
            Glide.with(context).load(image1file.getUrl()).into(ivRImage1);
            Log.d(TAG, "Loaded Image 1");
            context = ivRImage2.getContext();
            ParseFile image2file = (images.get(index)).getImage2Url();
            Glide.with(context).load(image2file.getUrl()).into(ivRImage2);
            Log.d(TAG, "Loaded Image 2");
            context = ivRImage3.getContext();
            ParseFile image3file = (images.get(index)).getImage3Url();
            Glide.with(context).load(image3file.getUrl()).into(ivRImage3);
            Log.d(TAG, "Loaded Image 3");
        }
    }
}