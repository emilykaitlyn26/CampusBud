package com.example.campusbud;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Interest")
public class Interest extends ParseObject {

    private static final String KEY_INTEREST = "interest";

    public Interest() {}

    public String getInterest() { return getString(KEY_INTEREST); }

    public void setInterest(String interest) { put(KEY_INTEREST, interest); }
}
