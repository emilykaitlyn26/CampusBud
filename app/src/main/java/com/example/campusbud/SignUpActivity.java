package com.example.campusbud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    public final String TAG = "SignUpActivity";
    public static final String authKey = "c523b47dfef8a387d934b40bbcf7d7bc5fe2c0ee";

    public EditText etNewUsername;
    public EditText etNewPassword;
    public Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick sign up button");
                String username = etNewUsername.getText().toString();
                String password = etNewPassword.getText().toString();
                if (!username.trim().equals("") && !password.trim().equals("")) {
                    try {
                        signUpUser(username, password);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Username/ password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signUpUser(String username, String password) throws IOException {
        Log.i(TAG, "Attempting to sign up user" + username);
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.signUpInBackground(e -> {
            if (e != null) {
                Log.e(TAG, "Issue with sign up", e);
                Toast.makeText(this, "Issue with sign up", Toast.LENGTH_SHORT).show();
                return;
            }
            //String UID = parseUser.KEY_OBJECT_ID;
            String UID = parseUser.getObjectId();
            User user = new User();
            user.setUid(UID);
            user.setName(username);

            /*try {
                createCometChatUser(UID, username);
            } catch (IOException | JSONException ex) {
                ex.printStackTrace();
            }*/

            CometChat.createUser(user, authKey, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d("createUser", user.toString());
                }

                @Override
                public void onError(CometChatException e) {
                    Log.e("createUser", e.getMessage());
                }
            });

            goMainActivity();
            Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT).show();
        });
    }

    /*public void createCometChatUser(String UID, String username) throws IOException, JSONException {
        /*OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"uid\":\"" + UID + "\", \"name\":\"" + username + "\"}");
        Request request = new Request.Builder()
                .url("https://appId.api-region.cometchat.io/v3/users")
                .post(body)
                .addHeader("apiKey", "49438afd893ffc776b52ecc4c78c297dbec336b6")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        User user = new User();

        String jsonData = response.body().string();
        JSONObject json = new JSONObject(jsonData);

        //String uid;
        user.setUid(json.getString("uid"));
        user.setName(json.getString("name"));*/

        /*String url = "https://appId.api-region.cometchat.io/v3/users";

        try {
            doGetRequest(url, UID, username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return user;
    }*/

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*void doGetRequest(String url, String UID, String username) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"uid\":\"" + UID + "\", \"name\":\"" + username + "\"}");

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apiKey", "49438afd893ffc776b52ecc4c78c297dbec336b6")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "Failed");
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();

                User user = new User();

                String jsonData = response.body().string();
                JSONObject json = null;
                try {
                    json = new JSONObject(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    user.setUid(json.getString("uid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    user.setName(json.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
}