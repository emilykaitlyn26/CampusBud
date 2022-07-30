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

import com.example.campusbud.models.Interest;
import com.example.campusbud.models.Trie;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CreateInterestsActivity extends AppCompatActivity {

    private TextView mTvInterest1;
    private TextView mTvInterest2;
    private TextView mTvInterest3;
    private TextView mTvErrorMessage;
    private EditText mEtAddInterest;
    private Button mBtnAdd;
    private List<Interest> mAllInterests;
    private List<String> mInterestList;
    private String mUserInput;
    private Trie mInterestsTrie;
    private List<String> mSuggestedWords;
    private ListView mListView;
    private int mInterestCounter;
    private TextView mTvErrorLengthInterest;

    private static final String TAG = "InterestsActivity";
    private static final int MAX_LENGTH = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        String mUsername = getIntent().getStringExtra("username");
        String mPassword = getIntent().getStringExtra("password");
        String mCollege = getIntent().getStringExtra("college");
        String mYear = getIntent().getStringExtra("year");
        String mMajor = getIntent().getStringExtra("major");
        String mName = getIntent().getStringExtra("name");
        String mGender = getIntent().getStringExtra("gender");

        mInterestCounter = 0;
        mBtnAdd = findViewById(R.id.btnAddInterest);
        Button mBtnContinueActivities = findViewById(R.id.btnContinueActivities);
        mTvErrorMessage = findViewById(R.id.tvErrorMessageInterest);
        mTvErrorLengthInterest = findViewById(R.id.tvErrorLengthInterest);
        mEtAddInterest = findViewById(R.id.etEnterInterest);
        mTvInterest1 = findViewById(R.id.tvInterest1);
        mTvInterest2 = findViewById(R.id.tvInterest2);
        mTvInterest3 = findViewById(R.id.tvInterest3);
        mAllInterests = new ArrayList<>();
        mSuggestedWords = new ArrayList<>();
        mInterestList = new ArrayList<>();

        mTvErrorMessage.setVisibility(View.GONE);
        mTvErrorLengthInterest.setVisibility(View.GONE);
        queryInterests();

        mEtAddInterest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mUserInput = mEtAddInterest.getText().toString();
                mInterestsTrie = new Trie(mInterestList);
                mSuggestedWords = mInterestsTrie.suggest(mUserInput);
                if (!mSuggestedWords.contains(mUserInput)) {
                    mBtnAdd.setVisibility(View.VISIBLE);
                } else {
                    mBtnAdd.setVisibility(View.GONE);
                }
                createList(mSuggestedWords, mUserInput);
                mListView.setOnItemClickListener((parent, view, position, id) -> {
                    String mInterest = mListView.getItemAtPosition(position).toString();
                    mInterestCounter += 1;
                    if (mInterestCounter == 1) {
                        mTvInterest1.setText(mInterest);
                    } else if (mInterestCounter == 2) {
                        mTvInterest2.setText(mInterest);
                    } else if (mInterestCounter == 3) {
                        mTvInterest3.setText(mInterest);
                    }
                });
            }
        });

        mBtnAdd.setOnClickListener(v -> {
            if (mUserInput.length() > MAX_LENGTH) {
                mTvErrorLengthInterest.setVisibility(View.VISIBLE);
            } else {
                addInterest(mUserInput);
                mSuggestedWords = mInterestsTrie.suggest(mUserInput);
                mSuggestedWords.add(mUserInput);
                createList(mSuggestedWords, mUserInput);
            }
        });

        mBtnContinueActivities.setOnClickListener(v -> {
            if (mInterestCounter == 3) {
                mTvErrorMessage.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), CreateActivitiesActivity.class);
                intent.putExtra("username", mUsername);
                intent.putExtra("password", mPassword);
                intent.putExtra("college", mCollege);
                intent.putExtra("year", mYear);
                intent.putExtra("name", mName);
                intent.putExtra("gender", mGender);
                intent.putExtra("major", mMajor);
                intent.putExtra("interest1", mTvInterest1.getText());
                intent.putExtra("interest2", mTvInterest2.getText());
                intent.putExtra("interest3", mTvInterest3.getText());
                startActivity(intent);
                finish();
            } else {
                mTvErrorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createList(List<String> words, String input) {
        ArrayAdapter<String> mInterestsAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, words);
        mListView = findViewById(R.id.lvInterests);
        if (input.equals("")) {
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            mListView.setAdapter(mInterestsAdapter);
        }
    }

    private void addInterest(String interest) {
        Interest mNewInterest = new Interest();
        mNewInterest.setInterest(interest);
        mNewInterest.saveInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Error while saving", e);
            }
            Log.i(TAG, "Save was successful!");
        });
    }

    private void queryInterests() {
        ParseQuery<Interest> query = ParseQuery.getQuery(Interest.class);
        query.findInBackground((objects, e) -> {
            mAllInterests.addAll(objects);
            for (int i = 0; i < mAllInterests.size(); i++) {
                mInterestList.add(mAllInterests.get(i).getInterest());
            }
        });
    }

}