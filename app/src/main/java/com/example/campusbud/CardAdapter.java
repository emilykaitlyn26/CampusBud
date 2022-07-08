package com.example.campusbud;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CardAdapter extends BaseAdapter {

    private Context context;
    private List<User> userProfiles;

    private final String TAG = "CardAdapter";

    public ImageView image1;
    public ImageView image2;
    public ImageView image3;
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

    public CardAdapter(Context context, List<User> userProfiles) {
        this.context = context;
        this.userProfiles = userProfiles;
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
            User userProfile = userProfiles.get(position);
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
    }


}