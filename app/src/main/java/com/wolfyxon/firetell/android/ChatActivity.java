package com.wolfyxon.firetell.android;

import android.net.http.HttpEngine;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolfyxon.firetell.android.components.MessageView;
import com.wolfyxon.firetell.android.lib.Chat;
import com.wolfyxon.firetell.android.lib.HttpApi;
import com.wolfyxon.firetell.android.lib.Message;
import com.wolfyxon.firetell.android.lib.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {
    DrawerLayout main;
    ScrollView messageScroll;
    LinearLayout messageList;
    NavigationView sideMenu;
    EditText messageInp;
    ImageButton sendBtn;
    HttpApi api;
    DatabaseReference db;
    FirebaseAuth auth;
    Chat currentChat;
    DatabaseReference currentChatRef;
    ValueEventListener currentChatListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initDb();
        api = new HttpApi(Volley.newRequestQueue(this));

        main = findViewById(R.id.main);
        messageScroll = findViewById(R.id.message_scroll);
        messageList = findViewById(R.id.messages);
        sideMenu = findViewById(R.id.chat_menu);
        messageInp = findViewById(R.id.message_input);
        sendBtn = findViewById(R.id.send_btn);

        initSideMenu();

        sendBtn.setOnClickListener(l -> {
            sendMessage(messageInp.getText().toString());
        });
    }

    void initSideMenu() {
        findViewById(R.id.menu_btn).setOnClickListener(l -> {
            openSideMenu();
        });

        OnBackPressedCallback backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openSideMenu();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, backCallback);
    }

    void sendMessage(String content) {
        String url = "chats/" + currentChat.id + "/messages";

        JSONObject body = new JSONObject();
        try {
            body.put("content", content);
        } catch (JSONException ignored) {

        };

        api.authRequest(url, Request.Method.POST, body,
                res -> {

                },
                err -> {
                    Util.showToast(this, err.getMessage());
                });
    }

    void clearMessages() {
        messageList.removeAllViews();
    }

    void addMessage(Message msg) {
        MessageView view = new MessageView(this, msg);
        view.fetchUser(api);

        messageList.addView(view);

        messageScroll.post(() -> {
            messageScroll.fullScroll(View.FOCUS_DOWN);
        });
    }

    void loadChat(String id) {
        if(currentChatRef != null && currentChatListener != null) {
            currentChatRef.removeEventListener(currentChatListener);
        }

        currentChatRef = db.child("/messages/" + id).orderByChild("timestamp").limitToLast(64).getRef();
        currentChatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearMessages();

                for(DataSnapshot msgSnapshot : snapshot.getChildren() ) {
                    addMessage(new Message(msgSnapshot));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        currentChatRef.addValueEventListener(currentChatListener);
    }

    void initDb() {
        db = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        startChatListLoader();
    }

    void startChatListLoader() {
        String uid = auth.getUid();

        DatabaseReference ref = db.child("/users/" + uid + "/chatMembership");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Boolean> idMap = (HashMap<String, Boolean>) snapshot.getValue();

                if(idMap == null) {
                    System.out.println("IS NULL");
                    return;
                }

                Set<String> ids = idMap.keySet();

                for(String id : ids) {
                    currentChat = new Chat(id, "nope"); // testing!
                    loadChat(id);
                    sideMenu.getMenu().add(id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void openSideMenu() {
        main.openDrawer(sideMenu);
    }
}
