package com.wolfyxon.firetell.android;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ChatActivity extends AppCompatActivity {
    DrawerLayout main;
    NavigationView sideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        main = findViewById(R.id.main);
        sideMenu = findViewById(R.id.chat_menu);

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

    void openSideMenu() {
        main.openDrawer(sideMenu);
    }
}
