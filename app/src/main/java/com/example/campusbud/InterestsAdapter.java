package com.example.campusbud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mInterests;

    public InterestsAdapter(Context context, List<String> interests) {
        this.mContext = context;
        this.mInterests = interests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_interests, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestsAdapter.ViewHolder holder, int position) {
        String interest = mInterests.get(position);
        holder.bind(interest);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvInterest;

        public ViewHolder(View itemView) {
            super(itemView);
            tvInterest = itemView.findViewById(R.id.tvInterest);
        }

        public void bind(String interest) {
            tvInterest.setText(interest);
        }
    }
}