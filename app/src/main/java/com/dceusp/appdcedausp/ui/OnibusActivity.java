package com.dceusp.appdcedausp.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dceusp.appdcedausp.R;

/**
 * Created by yago_ on 25/02/2018.
 */

public class OnibusActivity extends AppCompatActivity {

    WebView webOnibus;
    FloatingActionButton fabBack;
    ProgressDialog dialog;

    String url;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onibus_activity);

        pref = getSharedPreferences("myConfig",0);

        url = getResources().getStringArray(R.array.onibus_campus)[pref.getInt("Campus",0)];

        webOnibus = findViewById(R.id.webOnibus);
        webOnibus.getSettings().setBuiltInZoomControls(true);

        if (url.equals("nope")) {
            webOnibus.setVisibility(View.GONE);
            ImageView aguarde = new ImageView(this);
            aguarde.setImageResource(R.drawable.aguarde);
            addContentView(aguarde, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            aguarde.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            webOnibus.loadUrl(url);
            dialog = new ProgressDialog(this);
            dialog.setMessage("Carregando...");
            dialog.show();
            trimURL();
            webOnibus.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    dialog.dismiss();
                }
            });
        }
        fabBack = findViewById(R.id.fabOnibus);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void trimURL() {
        // TODO Fazer a URL mais amigável pro aplicativo
    }
}
