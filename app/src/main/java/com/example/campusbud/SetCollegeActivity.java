package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusbud.models.State;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.ArrayList;
import java.util.List;

public class SetCollegeActivity extends AppCompatActivity {

    private ArrayAdapter<String> mUniadapter;
    private List<ParseObject> mListColleges;
    private List<String> mUniversityNames;
    private ParseObject mStateObject;
    private String mSelectedCollege;

    private static final String TAG = "SetCollegeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_college);

        String mUsername = getIntent().getStringExtra("username");
        String mPassword = getIntent().getStringExtra("password");
        String mState = getIntent().getStringExtra("state");
        ArrayList<State> mList = (ArrayList<State>) getIntent().getSerializableExtra("list");

        mListColleges = new ArrayList<>();
        mUniversityNames = new ArrayList<>();

        if (mList.size() == 56) {
            for (int i = 0; i < mList.size(); i++) {
                mStateObject = mList.get(i);
                String mStateName = mStateObject.getString("name");
                assert mStateName != null;
                if (mStateName.equals(mState)) {
                    break;
                }
            }

            ParseRelation<ParseObject> mRelation = mStateObject.getRelation("Universities");
            ParseQuery<ParseObject> query = mRelation.getQuery();
            query.findInBackground((objects, e) -> {
                if (e != null) {
                    Log.e(TAG, "Issue getting colleges");
                    return;
                }
                mListColleges.addAll(objects);
                for (int i = 0; i < mListColleges.size(); i++) {
                    ParseObject mCollegeObject = mListColleges.get(i);
                    String mName = mCollegeObject.getString("name");
                    mUniversityNames.add(mName);
                }
                mUniadapter.notifyDataSetChanged();
            });

            mUniadapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mUniversityNames);
            AutoCompleteTextView mUniTextView = findViewById(R.id.chooseCollege);
            mUniTextView.setThreshold(3);
            mUniTextView.setAdapter(mUniadapter);
            mUniTextView.setOnItemClickListener((parent, view, position, id) -> mSelectedCollege = (String) parent.getItemAtPosition(position));
        }

        Button mBtnContinueProfile = findViewById(R.id.btnContinueProfile);
        mBtnContinueProfile.setOnClickListener(v -> {
            createProfile(mUsername, mPassword, mSelectedCollege);
        });
    }

    private void createProfile(String username, String password, String college) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("college", college);
        startActivity(intent);
        finish();
    }
}