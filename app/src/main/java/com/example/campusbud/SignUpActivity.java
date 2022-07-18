package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    public final String TAG = "SignUpActivity";

    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnContinue;
    public String selectedState;

    public List<State> stateList;
    public List<String> allStates;
    ArrayAdapter<String> stateadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        queryStates();
        allStates = new ArrayList<>();
        stateList = new ArrayList<State>();

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        AutoCompleteTextView stateTextView = (AutoCompleteTextView) findViewById(R.id.selectState);
        stateadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, allStates);

        stateTextView.setThreshold(1);
        stateTextView.setAdapter(stateadapter);
        stateTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedState = (String) parent.getItemAtPosition(position);
            }
        });

        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick sign up button");
                String username = etNewUsername.getText().toString();
                String password = etNewPassword.getText().toString();
                if (!username.trim().equals("") && !password.trim().equals("") && selectedState != null) {
                    collegeSignUp(username, password, selectedState, stateList);
                } else {
                    Toast.makeText(SignUpActivity.this, "Username/ password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void queryStates() {
        ParseQuery<State> query = ParseQuery.getQuery(State.class);
        query.findInBackground(new FindCallback<State>() {
            @Override
            public void done(List<State> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting states", e);
                    return;
                }
                stateList.addAll(objects);
                for (int i = 0; i < stateList.size(); i++) {
                    ParseObject collegeObject = stateList.get(i);
                    String name = collegeObject.getString("name");
                    allStates.add(name);
                }
                stateadapter.notifyDataSetChanged();
            }
        });
    }

    public void collegeSignUp(String username, String password, String selectedState, List<State> stateList) {
        Intent intent = new Intent(this, SetCollegeActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("state", selectedState);
        intent.putExtra("list", (Serializable) stateList);
        startActivity(intent);
    }
}