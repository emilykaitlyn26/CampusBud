package com.example.campusbud.models;


import org.json.JSONException;
import org.json.JSONObject;

public class Profile {

    public String name;
    public String username;
    public String password;
    public String year;
    public String major;

    public Profile() {}

    public static Profile fromJson(JSONObject jsonObject) throws JSONException {
        Profile profile = new Profile();
        profile.name = jsonObject.getString("name");
        profile.year = jsonObject.getString("year");
        profile.major = jsonObject.getString("major");
        return profile;
    }


}
