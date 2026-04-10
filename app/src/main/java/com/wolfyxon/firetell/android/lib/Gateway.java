package com.wolfyxon.firetell.android.lib;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;

public class Gateway extends HttpApi {
    HashMap<String, Chat> chatCache;

    public Gateway(RequestQueue queue) {
        super(queue);
        chatCache = new HashMap<>();
    }

    public Gateway(Context ctx) {
        this(Volley.newRequestQueue(ctx));
    }

    public DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getMessagesRef(String chatId) {
        return getDbRef().child("/messages/" + chatId).orderByChild("timestamp").limitToLast(64).getRef();
    }

    public void createChatListListener(ValueEventListener listener) {
        String uid = getAuth().getUid();

        DatabaseReference ref = getDbRef().child("/users/" + uid + "/chatMembership");
        ref.addValueEventListener(listener);
    }

    public Chat getCachedChat(String id) {
        return chatCache.get(id);
    }

    public void fetchChat(String id, GenericListener<Chat> onValue, GenericListener<Exception> onErr) {
        DatabaseReference ref = getDbRef().child("chats").child(id);

        Chat cached = getCachedChat(id);

        if(cached != null) {
            onValue.onData(cached);
            return;
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = new Chat(snapshot);
                registerChat(chat);

                onValue.onData(chat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onErr.onData(error.toException());
            }
        });
    }

    public void registerChat(Chat chat) {
        chatCache.put(chat.id, chat);
    }

    public Collection<Chat> getCachedChats() {
        return chatCache.values();
    }

}
