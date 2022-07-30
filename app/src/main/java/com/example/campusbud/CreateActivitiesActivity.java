package com.example.campusbud;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusbud.models.Activity;
import com.example.campusbud.models.Trie;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CreateActivitiesActivity extends AppCompatActivity {

    private TextView mTvActivity1;
    private TextView mTvActivity2;
    private TextView mTvActivity3;
    private TextView mTvErrorMessageActivity;
    private EditText mEtAddActivity;
    private Button mBtnAddActivity;
    private List<Activity> mAllActivities;
    private List<String> mActivityList;
    private String mUserInput;
    private Trie mActivitiesTrie;
    private List<String> mSuggestedWords;
    private ListView mListView;
    private int mActivityCounter;
    private TextView mTvErrorLengthActivity;

    private static final String TAG = "ActivitiesActivity";
    private static final int MAX_LENGTH = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        String mUsername = getIntent().getStringExtra("username");
        String mPassword = getIntent().getStringExtra("password");
        String mCollege = getIntent().getStringExtra("college");
        String mYear = getIntent().getStringExtra("year");
        String mMajor = getIntent().getStringExtra("major");
        String mName = getIntent().getStringExtra("name");
        String mGender = getIntent().getStringExtra("gender");
        String mInterest1 = getIntent().getStringExtra("interest1");
        String mInterest2 = getIntent().getStringExtra("interest2");
        String mInterest3 = getIntent().getStringExtra("interest3");

        mActivityCounter = 0;
        mBtnAddActivity = findViewById(R.id.btnAddActivity);
        Button mBtnContinueBio = findViewById(R.id.btnContinueBio);
        mTvErrorMessageActivity = findViewById(R.id.tvErrorMessageActivity);
        mTvErrorLengthActivity = findViewById(R.id.tvErrorLengthActivity);
        mEtAddActivity = findViewById(R.id.etEnterActivity);
        mTvActivity1 = findViewById(R.id.tvActivity1);
        mTvActivity2 = findViewById(R.id.tvActivity2);
        mTvActivity3 = findViewById(R.id.tvActivity3);
        mAllActivities = new ArrayList<>();
        mSuggestedWords = new ArrayList<>();
        mActivityList = new ArrayList<>();

        mTvErrorMessageActivity.setVisibility(View.GONE);
        mTvErrorLengthActivity.setVisibility(View.GONE);
        queryActivities();

        mEtAddActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mUserInput = mEtAddActivity.getText().toString();
                mActivitiesTrie = new Trie(mActivityList);
                mSuggestedWords = mActivitiesTrie.suggest(mUserInput);
                if (!mSuggestedWords.contains(mUserInput)) {
                    mBtnAddActivity.setVisibility(View.VISIBLE);
                } else {
                    mBtnAddActivity.setVisibility(View.GONE);
                }
                createList(mSuggestedWords, mUserInput);
                mListView.setOnItemClickListener((parent, view, position, id) -> {
                    String mInterest = mListView.getItemAtPosition(position).toString();
                    mActivityCounter += 1;
                    if (mActivityCounter == 1) {
                        mTvActivity1.setText(mInterest);
                    } else if (mActivityCounter == 2) {
                        mTvActivity2.setText(mInterest);
                    } else if (mActivityCounter == 3) {
                        mTvActivity3.setText(mInterest);
                    }
                });
            }
        });

        mBtnAddActivity.setOnClickListener(v -> {
            if (mUserInput.length() > MAX_LENGTH) {
                mTvErrorLengthActivity.setVisibility(View.VISIBLE);
            } else {
                addActivity(mUserInput);
                mSuggestedWords = mActivitiesTrie.suggest(mUserInput);
                mSuggestedWords.add(mUserInput);
                createList(mSuggestedWords, mUserInput);
            }
        });

        mBtnContinueBio.setOnClickListener(v -> {
            if (mActivityCounter == 3) {
                mTvErrorMessageActivity.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), CreateRoommateActivity.class);
                intent.putExtra("username", mUsername);
                intent.putExtra("password", mPassword);
                intent.putExtra("college", mCollege);
                intent.putExtra("year", mYear);
                intent.putExtra("name", mName);
                intent.putExtra("gender", mGender);
                intent.putExtra("major", mMajor);
                intent.putExtra("interest1", mInterest1);
                intent.putExtra("interest2", mInterest2);
                intent.putExtra("interest3", mInterest3);
                intent.putExtra("activity1", mTvActivity1.getText());
                intent.putExtra("activity2", mTvActivity2.getText());
                intent.putExtra("activity3", mTvActivity3.getText());
                startActivity(intent);
                finish();
            } else {
                mTvErrorMessageActivity.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createList(List<String> words, String input) {
        ArrayAdapter<String> mInterestsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, words);
        mListView = findViewById(R.id.lvActivities);
        if (input.equals("")) {
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(mInterestsAdapter);
        }
    }

    private void addActivity(String activity) {
        Activity mNewActivity = new Activity();
        mNewActivity.setActivity(activity);
        mNewActivity.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving", e);
            }
            Log.i(TAG, "Save was successful!");
        });
    }

    private void queryActivities() {
        ParseQuery<Activity> query = new ParseQuery<>(Activity.class);
        query.findInBackground((objects, e) -> {
            mAllActivities.addAll(objects);
            for (int i = 0; i < mAllActivities.size(); i++) {
                mActivityList.add(mAllActivities.get(i).getActivity());
            }
        });
    }

}