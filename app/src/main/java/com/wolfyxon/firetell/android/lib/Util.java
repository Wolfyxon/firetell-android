package com.wolfyxon.firetell.android.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.wolfyxon.firetell.android.R;

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

    public static void showToast(Context ctx, int textResource) {
        String text = ctx.getString(textResource);

        showToast(ctx, text);
    }

    public static void showToast(Context ctx, String message) {
        Toast t = new Toast(ctx);
        t.setDuration(Toast.LENGTH_LONG);
        t.setText(message);
        t.show();
    }

    public static void showAlert(Context ctx, int textResource) {
        String text = ctx.getString(textResource);

        showAlert(ctx, text);
    }

    public static void showAlert(Context ctx, String text) {
        new AlertDialog.Builder(ctx)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setMessage(text)
                .show();
    }

    public static void showConfirm(Context ctx, int textResource, HttpApi.GenericListener<Boolean> callback) {
        String text = ctx.getString(textResource);

        showConfirm(ctx, text, callback);
    }

    public static void showConfirm(Context ctx, String text, HttpApi.GenericListener<Boolean> callback) {
        new AlertDialog.Builder(ctx)
                .setPositiveButton(R.string.yes, (dialog, i) -> {
                    callback.onData(true);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.no, (dialog, i) -> {
                    dialog.cancel();
                })
                .setOnCancelListener(l -> {
                    callback.onData(false);
                })
                .setMessage(text)
                .show();
    }
}
