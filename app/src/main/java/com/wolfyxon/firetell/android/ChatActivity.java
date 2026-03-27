package com.wolfyxon.firetell.android;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolfyxon.firetell.android.lib.Chat;

import java.util.HashMap;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {
    DrawerLayout main;
    NavigationView sideMenu;
    ImageButton sendBtn;
    DatabaseReference db;
    FirebaseAuth auth;
    Chat currentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initDb();

        main = findViewById(R.id.main);
        sideMenu = findViewById(R.id.chat_menu);
        sendBtn = findViewById(R.id.send_btn);

        initSideMenu();
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
