package com.wolfyxon.firetell.android.lib;

import com.google.firebase.database.DataSnapshot;

public class Chat {
    public String id;
    public String name;

    public Chat(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Chat(DataSnapshot snapshot) {
        Object nameObj = snapshot.child("name").getValue();

        if(nameObj != null) {
            this.name = nameObj.toString();
        }

        this.id = snapshot.getKey();
    }
}
