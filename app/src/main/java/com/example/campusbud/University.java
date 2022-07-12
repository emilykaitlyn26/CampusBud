package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Usuniversitieslist_University")
public class University extends ParseObject {

    public static final String KEY_UNIVERSITY = "name";
    public static final String KEY_UNIVERSITY_STATE = "state";

    public static final String TAG = "University";

    public University() {}

    public String getUniversity() {
        return getString(KEY_UNIVERSITY);
    }

    public String getUniversityState() {
        return getString(KEY_UNIVERSITY_STATE);
    }
}