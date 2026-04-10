package com.wolfyxon.firetell.android.lib;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Gateway extends HttpApi {

    public Gateway(RequestQueue queue) {
        super(queue);
    }

    public Gateway(Context ctx) {
        super(Volley.newRequestQueue(ctx));
    }

    public DatabaseReference getDbRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public void createChatListListener(ValueEventListener listener) {
        String uid = getAuth().getUid();

        DatabaseReference ref = getDbRef().child("/users/" + uid + "/chatMembership");
        ref.addValueEventListener(listener);
    }

}
