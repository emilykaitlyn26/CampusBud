package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private EditText mEtNewUsername;
    private EditText mEtNewPassword;
    private String mSelectedState;

    private List<State> mStateList;
    private List<String> mAllStates;
    private ArrayAdapter<String> mStateadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        queryStates();
        mAllStates = new ArrayList<>();
        mStateList = new ArrayList<>();

        mEtNewUsername = findViewById(R.id.etNewUsername);
        mEtNewPassword = findViewById(R.id.etNewPassword);
        AutoCompleteTextView mStateTextView = (AutoCompleteTextView) findViewById(R.id.selectState);
        mStateadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mAllStates);

        mStateTextView.setThreshold(1);
        mStateTextView.setAdapter(mStateadapter);
        mStateTextView.setOnItemClickListener((parent, view, position, id) -> mSelectedState = (String) parent.getItemAtPosition(position));

        Button mBtnContinue = findViewById(R.id.btnContinue);
        mBtnContinue.setOnClickListener(v -> {
            Log.i(TAG, "onClick sign up button");
            String mUsername = mEtNewUsername.getText().toString();
            String mPassword = mEtNewPassword.getText().toString();
            if (!mUsername.trim().equals("") && !mPassword.trim().equals("") && mSelectedState != null) {
                collegeSignUp(mUsername, mPassword, mSelectedState, mStateList);
            } else {
                Toast.makeText(SignUpActivity.this, "Username/ password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryStates() {
        ParseQuery<State> query = ParseQuery.getQuery(State.class);
        query.findInBackground((objects, e) -> {
            if (e != null) {
                Log.e(TAG, "Issue with getting states", e);
                return;
            }
            mStateList.addAll(objects);
            for (int i = 0; i < mStateList.size(); i++) {
                ParseObject collegeObject = mStateList.get(i);
                String name = collegeObject.getString("name");
                mAllStates.add(name);
            }
            mStateadapter.notifyDataSetChanged();
        });
    }

    private void collegeSignUp(String username, String password, String selectedState, List<State> stateList) {
        Intent intent = new Intent(this, SetCollegeActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("state", selectedState);
        intent.putExtra("list", (Serializable) stateList);
        startActivity(intent);
    }
}