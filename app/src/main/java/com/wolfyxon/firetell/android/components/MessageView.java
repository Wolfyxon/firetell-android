package com.wolfyxon.firetell.android.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wolfyxon.firetell.android.R;
import com.wolfyxon.firetell.android.lib.Message;

public class MessageView extends LinearLayout {
    Message message;

    TextView authorLabel;
    TextView textLabel;

    public MessageView(Context ctx) {
        super(ctx);
        init();
    }

    public MessageView(Context ctx, AttributeSet attributes) {
        super(ctx, attributes);
        init();
    }

    public MessageView(Context ctx, AttributeSet attributes, int defStyleAttr) {
        super(ctx, attributes, defStyleAttr);
        init();
    }

    public MessageView(Context ctx, AttributeSet attributes, int defStyleAttr, int defStyleRes) {
        super(ctx, attributes, defStyleAttr, defStyleRes);
        init();
    }

    public MessageView(Context ctx, Message message) {
        super(ctx);
        this.message = message;
        init();
    }

    void init() {
        inflate(getContext(), R.layout.message_view, this);

        authorLabel = findViewById(R.id.author_lbl);
        textLabel = findViewById(R.id.text_lbl);

        updateMessage();
    }

    void updateMessage() {
        if(message == null) {
            return;
        }

        authorLabel.setText(message.authorUid); // TODO: Fetch username
        textLabel.setText(message.content);
    }
}
