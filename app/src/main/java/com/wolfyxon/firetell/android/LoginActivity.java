package com.wolfyxon.firetell.android;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wolfyxon.firetell.android.lib.Util;

public class LoginActivity extends AppCompatActivity {
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_btn);

        loginBtn.setOnClickListener(l -> {
            Util.changeActivity(this, ChatActivity.class);
            finish();
        });
    }
}
