package com.example.campusbud.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.example.campusbud.MainActivity;
import com.example.campusbud.R;

public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";

    public ChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.cometchat.pro.uikit.R.layout.fragment_cometchat_conversationlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //startActivity(new Intent(getActivity(), CometChatUI.class));
    }
}