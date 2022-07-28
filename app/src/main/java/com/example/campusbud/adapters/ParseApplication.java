package com.example.campusbud.adapters;

import android.app.Application;

import com.example.campusbud.BuildConfig;
import com.example.campusbud.Activity;
import com.example.campusbud.Image;
import com.example.campusbud.Interest;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    private static final String PARSE_ID = BuildConfig.PARSE_ID;
    private static final String CLIENT_KEY = BuildConfig.CLIENT_KEY;
    private static final String SERVER = BuildConfig.SERVER;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Image.class);
        ParseObject.registerSubclass(Interest.class);
        ParseObject.registerSubclass(Activity.class);

        Parse.initialize(new Parse.Configuration.Builder(this).applicationId(PARSE_ID).clientKey(CLIENT_KEY).server(SERVER).build());
    }
}