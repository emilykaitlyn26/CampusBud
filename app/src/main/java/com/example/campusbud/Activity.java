package com.example.campusbud;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Activity")
public class Activity extends ParseObject {

    private static final String KEY_ACTIVITY = "activity";

    public Activity() {}

    public String getActivity() { return getString(KEY_ACTIVITY); }

    public void setActivity(String activity) { put(KEY_ACTIVITY, activity); }
}
