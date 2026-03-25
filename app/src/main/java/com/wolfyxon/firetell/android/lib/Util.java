package com.wolfyxon.firetell.android.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Util {
    public static void pushActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        ctx.startActivity(intent);
    }

    public static void changeActivity(Activity currentActivity, Class<?> cls) {
        Intent intent = new Intent(currentActivity, cls);
        currentActivity.startActivity(intent);

        currentActivity.finish();
    }
}
