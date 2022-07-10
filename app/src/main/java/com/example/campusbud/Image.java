package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;

@ParseClassName("Image")
public class Image extends ParseObject {

    public static final String KEY_PROFILE_IMAGE = "ProfileImage";
    public static final String KEY_IMAGE1 = "Image1";
    public static final String KEY_IMAGE2 = "Image2";
    public static final String KEY_IMAGE3 = "Image3";
    public static final String KEY_USER = "User";

    public static final String TAG = "Image";

    public Image() {}

    public ParseFile getProfileImageUrl() { return getParseFile(KEY_PROFILE_IMAGE); }

    public void setProfileImage(ParseFile parseFile) { put(KEY_PROFILE_IMAGE, parseFile); }

    public ParseFile getImage1Url() { return getParseFile(KEY_IMAGE1); }

    public void setImage1(ParseFile parseFile) { put(KEY_IMAGE1, parseFile); }

    public ParseFile getImage2Url() { return getParseFile(KEY_IMAGE2); }

    public void setImage2(ParseFile parseFile) { put(KEY_IMAGE2, parseFile); }

    public ParseFile getImage3Url() { return getParseFile(KEY_IMAGE3); }

    public void setImage3(ParseFile parseFile) { put(KEY_IMAGE3, parseFile); }

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }
}