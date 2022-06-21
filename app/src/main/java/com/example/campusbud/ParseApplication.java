package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this).applicationId("cggD92fB7eLYVy9N9nK7vHBBuCbotEkYIJnE7lef").clientKey("FUTmG8TUxQ7fxtbVt6g9HslZtKibqWcaE7VSBpYE").server("https://parseapi.back4app.com").build());
    }
}