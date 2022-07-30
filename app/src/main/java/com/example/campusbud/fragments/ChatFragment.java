package com.example.campusbud.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.uikit.databinding.ActivityCometchatUnifiedBinding;
import com.cometchat.pro.uikit.ui_resources.utils.CometChatError;

import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;
import java.util.List;

import com.cometchat.pro.uikit.ui_components.calls.call_manager.listener.CometChatCallListener;
import com.cometchat.pro.uikit.ui_components.chats.CometChatConversationList;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    @SuppressLint("StaticFieldLeak")
    private static ActivityCometchatUnifiedBinding activityCometChatUnifiedBinding;

    private final List<String> mUnreadCount = new ArrayList<>();

    private final BadgeDrawable mBadgeDrawable;

    @VisibleForTesting
    public static AppCompatActivity activity;

    public ChatFragment(BadgeDrawable mBadgeDrawable) {
        this.mBadgeDrawable = mBadgeDrawable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(com.cometchat.pro.uikit.R.layout.fragment_cometchat_conversationlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        CometChatError.init(getContext());
        if (!CometChatCallListener.isInitialized)
            CometChatCallListener.addCallListener(TAG, getContext());

        setConversationClickListener();
    }

    private void setConversationClickListener() {
        CometChatConversationList.setItemClickListener(new OnItemClickListener<Conversation>() {
            @Override
            public void OnItemClick(Conversation conversation, int position) {
                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP))
                    startGroupIntent(((Group) conversation.getConversationWith()));
            }
        });
    }

    private void startGroupIntent(Group group) {
        Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
        intent.putExtra(UIKitConstants.IntentStrings.GUID, group.getGuid());
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, group.getIcon());
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER,group.getOwner());
        intent.putExtra(UIKitConstants.IntentStrings.NAME, group.getName());
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE,group.getGroupType());
        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        intent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT,group.getMembersCount());
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC,group.getDescription());
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD,group.getPassword());
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        addConversationListener();
    }

    public void addConversationListener() {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                setUnreadCount(message);
            }
            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                setUnreadCount(message);
            }
            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                setUnreadCount(message);
            }
        });
    }

    private void setUnreadCount(BaseMessage message) {
        if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            if (!mUnreadCount.contains(message.getReceiverUid())) {
                mUnreadCount.add(message.getReceiverUid());
                setBadge();
            }
        } else {

            if (!mUnreadCount.contains(message.getSender().getUid())) {
                mUnreadCount.add(message.getSender().getUid());
                setBadge();
            }
        }
    }

    private void setBadge(){
        mBadgeDrawable.setVisible(mUnreadCount.size() != 0);
        mBadgeDrawable.setNumber(mBadgeDrawable.getNumber() + 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBadgeDrawable.clearNumber();
        mUnreadCount.clear();
    }

    @VisibleForTesting
    public static ActivityCometchatUnifiedBinding getBinding() {
        return activityCometChatUnifiedBinding;
    }

    @VisibleForTesting
    public static AppCompatActivity getCometChatUIActivity() {
        return activity;
    }
}