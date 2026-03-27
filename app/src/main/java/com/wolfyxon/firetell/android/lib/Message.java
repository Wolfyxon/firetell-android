package com.wolfyxon.firetell.android.lib;

import com.google.firebase.database.DataSnapshot;

public class Message {
    public String content;
    public String authorUid;

    public Message(DataSnapshot snapshot) {
        Object contentVal = snapshot.child("content").getValue();
        Object uidVal = snapshot.child("uid").getValue();

        assert contentVal != null;
        assert uidVal != null;

        content = contentVal.toString();
        authorUid = uidVal.toString();
    }

    public Message(String content, String authorUid) {
        this.content = content;
        this.authorUid = authorUid;
    }
}
