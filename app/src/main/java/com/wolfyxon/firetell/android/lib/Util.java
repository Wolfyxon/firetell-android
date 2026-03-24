package com.wolfyxon.firetell.android.lib;

import android.content.Context;
import android.content.Intent;

public class Util {
    public static void changeActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        ctx.startActivity(intent);
    }
}
