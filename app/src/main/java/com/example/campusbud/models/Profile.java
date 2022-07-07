package com.example.campusbud.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Profile {

    public String name;
    public String username;
    public String password;
    public String year;
    public String major;
    /*public String cleanliness;
    public String ifSmoke;
    public String ifDrink;
    public Boolean ifRoommateProfile = false;*/

    //public static final String KEY_OBJECT_ID = "object_id";

    public Profile() {}

    public static Profile fromJson(JSONObject jsonObject) throws JSONException {
        Profile profile = new Profile();
        profile.name = jsonObject.getString("name");
        profile.year = jsonObject.getString("year");
        profile.major = jsonObject.getString("major");
        /*if (jsonObject.has("roommate_profile")) {
            profile.ifRoommateProfile = true;
            JSONObject roommateProfile = jsonObject.getJSONObject("roommate_profile");
            profile.cleanliness = roommateProfile.getString("cleanliness");
            profile.ifSmoke = roommateProfile.getString("if_smoke");
            profile.ifDrink= roommateProfile.getString("if_drink");
        }*/
        return profile;
    }

    public static List<Profile> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Profile> profiles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            profiles.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return profiles;
    }
}
