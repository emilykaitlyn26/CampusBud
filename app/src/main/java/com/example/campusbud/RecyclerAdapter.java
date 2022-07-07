package com.example.campusbud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cometchat.pro.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private List<User> users;

    private final String TAG = "RecyclerAdapter";

    public RecyclerAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_adapter, parent, false);
        return new RecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        User user = users.get(position);
        try {
            holder.bind(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRName = itemView.findViewById(R.id.tvRName);
        }

        public void bind(User user) throws JSONException {
            JSONObject userMetadata = user.getMetadata();
            Log.d(TAG, "user metadata: " + userMetadata);
            tvRName.setText(userMetadata.getString("name"));
            //tvRName.setText(user.getName());
        }
    }
}