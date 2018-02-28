package com.example.appdcedausp.ui;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.appdcedausp.R;

/**
 * Created by yago_ on 26/02/2018.
 */

public class MedicoActivity extends AppCompatActivity {

    WebView webMedico;
    FloatingActionButton fabBack;
    ProgressDialog dialog;

    String url;

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medico_activity);

        pref = getSharedPreferences("myConfig",0);

        url = getResources().getStringArray(R.array.atendimento_campus)[pref.getInt("Campus",0)];

        webMedico = findViewById(R.id.webMedico);
        webMedico.loadUrl(url);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Carregando...");
        dialog.show();
        trimURL();
        webMedico.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }
        });

        fabBack = findViewById(R.id.fabMedico);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void trimURL() {

    }
}
