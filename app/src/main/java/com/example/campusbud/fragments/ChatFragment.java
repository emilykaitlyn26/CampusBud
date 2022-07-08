package com.example.campusbud.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
//import androidx.emoji.bundled.BundledEmojiCompatConfig;
//import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.databinding.ActivityCometchatUnifiedBinding;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_components.shared.CometChatSnackBar;
import com.cometchat.pro.uikit.ui_resources.utils.CometChatError;
import com.cometchat.pro.uikit.ui_resources.utils.EncryptionUtils;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.cometchat.pro.uikit.ui_components.calls.call_list.CometChatCallList;
import com.cometchat.pro.uikit.ui_components.calls.call_manager.listener.CometChatCallListener;
import com.cometchat.pro.uikit.ui_components.chats.CometChatConversationList;
import com.cometchat.pro.uikit.ui_components.groups.group_list.CometChatGroupList;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_components.userprofile.CometChatUserProfile;
import com.cometchat.pro.uikit.ui_components.users.user_list.CometChatUserList;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.custom_alertDialog.CustomAlertDialogHelper;
import com.cometchat.pro.uikit.ui_resources.utils.custom_alertDialog.OnAlertDialogButtonClickListener;
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;
import com.cometchat.pro.uikit.ui_settings.FeatureRestriction;


public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";

    private static ActivityCometchatUnifiedBinding activityCometChatUnifiedBinding;

    private List<String> unreadCount = new ArrayList<>();

    private BadgeDrawable badgeDrawable;

    private Fragment fragment;

    private ProgressDialog progressDialog;

    private String groupPassword;

    private Group group;

    private Fragment active = new CometChatConversationList();

    private boolean isUserListVisible;
    private boolean isConversationVisible;
    private boolean isSettingsVisible;
    private boolean isCallsListVisible;
    private boolean isGroupsListVisible;

    @VisibleForTesting
    public static AppCompatActivity activity;

    public ChatFragment() {}

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
            if (!unreadCount.contains(message.getReceiverUid())) {
                unreadCount.add(message.getReceiverUid());
                setBadge();
            }
        } else {

            if (!unreadCount.contains(message.getSender().getUid())) {
                unreadCount.add(message.getSender().getUid());
                setBadge();
            }
        }
    }

    private void setBadge(){
        if (unreadCount.size()==0){
            badgeDrawable.setVisible(false);
        } else
            badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(badgeDrawable.getNumber() + 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        badgeDrawable.clearNumber();
        unreadCount.clear();
    }

    @VisibleForTesting
    public static ActivityCometchatUnifiedBinding getBinding() {
        return activityCometChatUnifiedBinding;
    }

    @VisibleForTesting
    public static AppCompatActivity getCometChatUIActivity() {
        return activity;
    }

    public static void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}