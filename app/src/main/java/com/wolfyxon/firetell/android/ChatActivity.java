package com.wolfyxon.firetell.android;

import android.os.Bundle;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wolfyxon.firetell.android.components.MessageView;
import com.wolfyxon.firetell.android.lib.Chat;
import com.wolfyxon.firetell.android.lib.Gateway;
import com.wolfyxon.firetell.android.lib.Message;
import com.wolfyxon.firetell.android.lib.Util;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatActivity extends AppCompatActivity {
    DrawerLayout main;
    TextView chatNameLbl;
    ScrollView messageScroll;
    LinearLayout messageList;
    NavigationView sideMenu;
    EditText messageInp;
    ImageButton sendBtn;

    Gateway gateway;
    DatabaseReference db;
    FirebaseAuth auth;
    Chat currentChat;
    DatabaseReference currentChatRef;
    ValueEventListener currentChatListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        gateway = new Gateway(this);

        loadUi();
        initDb();
        initSideMenu();

        sendBtn.setOnClickListener(l -> {
            sendMessage();
        });
    }

    void loadUi() {
        main = findViewById(R.id.main);
        chatNameLbl = findViewById(R.id.chat_name);
        messageScroll = findViewById(R.id.message_scroll);
        messageList = findViewById(R.id.messages);
        sideMenu = findViewById(R.id.chat_menu);
        messageInp = findViewById(R.id.message_input);
        sendBtn = findViewById(R.id.send_btn);
    }

    void initSideMenu() {
        FirebaseUser user = auth.getCurrentUser();

        if(user != null) {
            String displayName = user.getDisplayName();

            TextView usernameLbl = getSideMenuHeader().findViewById(R.id.username);
            usernameLbl.setText(displayName != null ? displayName : user.getUid());
        }

        findViewById(R.id.menu_btn).setOnClickListener(l -> {
            openSideMenu();
        });

        getSideMenuHeader().findViewById(R.id.logout_btn).setOnClickListener(l -> {
           Util.showConfirm(this, "Would you like to log out?", res -> {
               if(res) {
                   auth.signOut();
                   Util.changeActivity(this, LoginActivity.class);
               }
           });
        });

        OnBackPressedCallback backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                openSideMenu();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, backCallback);
    }

    void sendMessage() {
        if(currentChat == null) {
            Util.showToast(this, "No chat selected");
            return;
        }

        String content = messageInp.getText().toString();

        if(content.isEmpty()) {
            return;
        }

        gateway.sendMessage(currentChat.id, content,
                res -> {},
                err -> {
                    Util.showToast(this, err.getMessage());
                }
        );

        messageInp.setText("");
    }

    void clearMessages() {
        messageList.removeAllViews();
    }

    void addMessage(Message msg) {
        auth = FirebaseAuth.getInstance();

        MessageView view = new MessageView(this, msg, auth.getUid() != null && auth.getUid().equals(msg.authorUid));
        view.fetchUser(gateway);

        messageList.addView(view);

        messageScroll.post(() -> {
            messageScroll.fullScroll(View.FOCUS_DOWN);
        });
    }

    void selectChat(String id) {
        if(currentChatRef != null && currentChatListener != null) {
            currentChatRef.removeEventListener(currentChatListener);
        }

        gateway.fetchChat(id,
                chat -> {
                    String name = chat.name;

                    if(name == null) {
                        name = "Unknown chat";
                    }

                    chatNameLbl.setText(name);
                    currentChat = chat;
                },
                err -> {
                    Util.showToast(this, "Failed to load chat: " + err.getMessage());
                }
        );

        currentChatRef = gateway.getMessagesRef(id);

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
                Util.showAlert(getApplicationContext(), "Failed to load messages:\n" + error.getMessage());
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
        AtomicBoolean firstLoad = new AtomicBoolean(true);

        gateway.createChatListListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getChatListMenu().clear();
                HashMap<String, Boolean> idMap = (HashMap<String, Boolean>) snapshot.getValue();

                if(idMap == null) {
                    System.out.println("IS NULL");
                    return;
                }

                Set<String> ids = idMap.keySet();

                for(String id : ids) {
                    gateway.fetchChat(id,
                        chat -> {
                            addChat(chat);

                            if(firstLoad.get()) {
                                selectChat(id);
                                firstLoad.set(false);
                            }
                        },
                        err -> {}
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showAlert(getApplicationContext(), "Failed to load chats:\n" + error.getMessage());
            }
        });
    }

    SubMenu getChatListMenu() {
        return sideMenu.getMenu().findItem(R.id.chats).getSubMenu();
    }

    View getSideMenuHeader() {
        return sideMenu.getHeaderView(0);
    }

    void addChat(Chat chat) {
        String name = chat.name != null ? chat.name : "Unknown chat";

        getChatListMenu().add(name).setOnMenuItemClickListener(l -> {
            selectChat(chat.id);
            main.close();
            return true;
        });
    }

    void openSideMenu() {
        main.openDrawer(sideMenu);
    }
}
