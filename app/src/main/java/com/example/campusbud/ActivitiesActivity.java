package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;

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

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

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

    private static final String TAG = "ActivitiesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        mActivityCounter = 0;
        mBtnAddActivity = findViewById(R.id.btnAddActivity);
        Button mBtnContinueBio = findViewById(R.id.btnContinueBio);
        mTvErrorMessageActivity = findViewById(R.id.tvErrorMessageActivity);
        mEtAddActivity = findViewById(R.id.etEnterActivity);
        mTvActivity1 = findViewById(R.id.tvActivity1);
        mTvActivity2 = findViewById(R.id.tvActivity2);
        mTvActivity3 = findViewById(R.id.tvActivity3);
        mAllActivities = new ArrayList<>();
        mSuggestedWords = new ArrayList<>();
        mActivityList = new ArrayList<>();

        mTvErrorMessageActivity.setVisibility(View.GONE);
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
            addActivity(mUserInput);
            mSuggestedWords = mActivitiesTrie.suggest(mUserInput);
            mSuggestedWords.add(mUserInput);
            createList(mSuggestedWords, mUserInput);
        });

        mBtnContinueBio.setOnClickListener(v -> {
            if (mActivityCounter == 3) {
                mTvErrorMessageActivity.setVisibility(View.GONE);
                User mUser = CometChat.getLoggedInUser();
                JSONObject mMetadata = mUser.getMetadata();
                try {
                    JSONArray mRoommateProfileArray = mMetadata.getJSONArray("roommate_profile");
                    JSONObject mRoommateProfile = mRoommateProfileArray.getJSONObject(0);
                    mRoommateProfile.put("activity1", mTvActivity1.getText());
                    mRoommateProfile.put("activity2", mTvActivity2.getText());
                    mRoommateProfile.put("activity3", mTvActivity3.getText());
                    updateUser(mUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), BioActivity.class);
                startActivity(intent);
                finish();
            } else {
                mTvErrorMessageActivity.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createList(List<String> words, String input) {
        ArrayAdapter<String> mInterestsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, words);
        mListView = (ListView) findViewById(R.id.lvActivities);
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

    private void updateUser(User user) {
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, user.toString());
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, e.getMessage());
            }
        });
    }
}