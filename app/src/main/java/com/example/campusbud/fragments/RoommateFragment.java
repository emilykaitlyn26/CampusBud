package com.example.campusbud.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.UsersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.campusbud.CardAdapter;
import com.example.campusbud.CardStackCallback;
import com.example.campusbud.ItemModel;
import com.example.campusbud.R;
import com.example.campusbud.RecyclerAdapter;
import com.yalantis.library.Koloda;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoommateFragment extends Fragment {

    /*private static final String TAG = "RoommateFragment";

    private RecyclerView rvRoommate;
    private RecyclerAdapter adapter;
    public List<User> allUsers;

    public RoommateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roommate, container, false);
    }*/

    /*@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRoommate = view.findViewById(R.id.rvRoommate);
        allUsers = new ArrayList<>();
        queryUsers();
        //allUsers = queryUsers();
        adapter = new RecyclerAdapter(getContext(), allUsers);
        rvRoommate.setAdapter(adapter);
        rvRoommate.setLayoutManager(new LinearLayoutManager(getContext()));
        /*try {
            debug(allUsers);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void queryUsers() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "User list received: " + users.size());
                Log.d(TAG, "Users" + users);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    public void debug(List<User> users) throws JSONException {
        User user = users.get(0);
        Log.d(TAG, "user: " + user);
        JSONObject metadata = user.getMetadata();
        Log.d(TAG, "user metadata: " + metadata);
        String name = metadata.getString("name");
        Log.d(TAG, "user name: " + name);
    }*/

    /*private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    private static final String TAG = "RoommateFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_roommate, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        CardStackView cardStackView = root.findViewById(R.id.cardStackView);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }
            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + "d=" + direction);
                if (direction == Direction.Right) {
                    Toast.makeText(getContext(), "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top) {
                    Toast.makeText(getContext(), "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
                    Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom) {
                    Toast.makeText(getContext(), "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                if (manager.getTopPosition() == adapter.getItemCount() - 5) {
                    paginate();
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardCanceled " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardApperared: " + position + ", name: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        List<ItemModel> old = adapter.getItems();
        List<ItemModel> other = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, other);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(other);
        result.dispatchUpdatesTo(adapter);
    }

    private List<ItemModel> addList() {
        List<ItemModel> items = new ArrayList<>();
        return items;
    }
 */
    public Koloda adapter;
    public CardAdapter cardAdapter;

    private final String TAG = "RoommateFragment";

    public List<User> allUsers;

    public RoommateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roommate, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = (Koloda) view.findViewById(R.id.koloda);
        allUsers = new ArrayList<>();
        queryUsers();
        cardAdapter = new CardAdapter(getContext(), allUsers);
        adapter.setAdapter(cardAdapter);
        Log.d(TAG, "profiles: " + allUsers);
    }

    public void queryUsers() {
        UsersRequest usersRequest = new UsersRequest.UsersRequestBuilder().build();
        usersRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                allUsers.addAll(users);
                cardAdapter.notifyDataSetChanged();
                Log.d(TAG, "User list received: " + users.size());
                Log.d(TAG, "Users" + users);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "User list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    /*protected void queryProfiles() {
        ParseQuery<Profile> query = ParseQuery.getQuery(Profile.class);
        query.include(Profile.KEY_OBJECT_ID);
        query.whereEqualTo(Profile.KEY_OBJECT_ID, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Profile>() {
            @Override
            public void done(List<Profile> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting profiles", e);
                    return;
                }
                for (Profile profile : objects) {
                    Log.i(TAG, "Profile: " + profile.name);
                }
                allProfiles.addAll(objects);
                cardAdapter.notifyDataSetChanged();
            }
        });
    }*/
}