package com.example.campusbud;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class CreateProfileActivity extends AppCompatActivity {

    private String mYear;
    private RadioButton mRbYear;
    private String mGender;

    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_MAJOR_LENGTH = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        String mUsername = getIntent().getStringExtra("username");
        String mPassword = getIntent().getStringExtra("password");
        String mCollege = getIntent().getStringExtra("college");

        TextInputLayout mLayoutName = findViewById(R.id.layoutName);
        TextInputLayout mLayoutMajor = findViewById(R.id.layoutMajor);
        RadioGroup mRgYears = findViewById(R.id.rgYears);
        RadioGroup mRgGender = findViewById(R.id.rgGender);
        Button mBtnContinueInterests = findViewById(R.id.btnContinueInterests);
        TextView mTvNameError = findViewById(R.id.tvNameError);
        TextView mTvMajorError = findViewById(R.id.tvMajorError);
        TextView mTvErrorAll = findViewById(R.id.tvErrorAll);

        mTvErrorAll.setVisibility(View.GONE);
        mTvNameError.setVisibility(View.GONE);
        mTvMajorError.setVisibility(View.GONE);

        mRgYears.setOnCheckedChangeListener((group, checkedId) -> {
            mRbYear = findViewById(checkedId);
            mYear = mRbYear.getText().toString();
        });

        mRgGender.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton mRbGender = findViewById(checkedId);
            mGender = mRbGender.getText().toString();
        });

        mBtnContinueInterests.setOnClickListener(v -> {
            if (!(Objects.requireNonNull(mLayoutName.getEditText()).getText().toString().trim()).equals("") && !(Objects.requireNonNull(mLayoutMajor.getEditText()).getText().toString().trim()).equals("") && mYear != null && mGender != null) {
                String mName = mLayoutName.getEditText().getText().toString();
                String mMajor = mLayoutMajor.getEditText().getText().toString();
                goInterests(mUsername, mPassword, mCollege, mYear, mName, mGender, mMajor);
            } else {
                if (mLayoutName.getEditText().getText().toString().length() > MAX_NAME_LENGTH) {
                    mTvNameError.setVisibility(View.VISIBLE);
                }
                if (Objects.requireNonNull(mLayoutMajor.getEditText()).getText().toString().length() > MAX_MAJOR_LENGTH) {
                    mTvMajorError.setVisibility(View.VISIBLE);
                }
                if ((mLayoutName.getEditText().getText().toString().trim()).equals("") || (mLayoutMajor.getEditText().getText().toString().trim()).equals("") || mYear == null || mGender == null) {
                    mTvErrorAll.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void goInterests(String username, String password, String college, String year, String name, String gender, String major) {
        Intent intent = new Intent(this, CreateInterestsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("college", college);
        intent.putExtra("year", year);
        intent.putExtra("name", name);
        intent.putExtra("gender", gender);
        intent.putExtra("major", major);
        startActivity(intent);
        finish();
    }
}