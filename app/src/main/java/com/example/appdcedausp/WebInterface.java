package com.example.appdcedausp;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by yago_ on 15/01/2018.
 */

public class WebInterface {
    Context mContext;

    WebInterface(Context c) {
        mContext = c;
    }

    @SuppressWarnings("unused")
    public void showHTML(String html)
    {
        new AlertDialog.Builder(mContext)
                .setTitle("HTML")
                .setMessage(html)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show();
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}
